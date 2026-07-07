package com.mndk.bteterrarenderer.util.image;

import com.mndk.bteterrarenderer.util.IOUtil;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class ImageUtil {

    private final Transformer TRANSFORMER;
    private final DocumentBuilder DOCUMENT_BUILDER;
    private final NullPointerException NPE = new NullPointerException();

    public BufferedImage bufferToImage(ByteBuf buf, int width, int height) throws Exception {
        byte[] bytes = IOUtil.readAllBytes(buf);

        // Try bitmap type image
        try {
            InputStream stream = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(stream);
            stream.close();

            if (image == null) throw NPE;
            if (width != -1 && height != -1) {
                image = ImageUtil.resizeImage(image, width, height);
            }
            return image;
        } catch (IOException | NullPointerException ignored) {}

        // Try svg type image
        InputStream svgStream = fixBrokenSvgFile(new ByteArrayInputStream(bytes));
        TranscoderInput svgInput = new TranscoderInput(svgStream);

        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        if (width != -1 && height != -1) {
             transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
             transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);
        }
        transcoder.transcode(svgInput, null);
        BufferedImage image = transcoder.getBufferedImage();
        svgStream.close();

        if (image == null) throw NPE;
        return image;
    }

    private InputStream fixBrokenSvgFile(InputStream brokenSvgStream) throws IOException, SAXException, TransformerException {
        // "The attribute 'offset' of the element <stop> is required" error handler
        Document svgDocument = DOCUMENT_BUILDER.parse(brokenSvgStream);
        NodeList stopTags = svgDocument.getElementsByTagName("stop");
        for (int i = 0; i < stopTags.getLength(); i++) {
            NamedNodeMap stopNodeAttributes = stopTags.item(i).getAttributes();
            Node offsetNode = stopNodeAttributes.getNamedItem("offset");
            if (offsetNode != null) continue;

            Attr newOffsetNode = svgDocument.createAttribute("offset");
            newOffsetNode.setValue("0");
            stopNodeAttributes.setNamedItem(newOffsetNode);
        }

        StringWriter writer = new StringWriter();
        TRANSFORMER.transform(new DOMSource(svgDocument), new StreamResult(writer));
        return new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
    }

    public BufferedImage resizeImage(@Nonnull BufferedImage image, int paletteWidth, int paletteHeight) {
        if (paletteWidth <= 0 || paletteHeight <= 0) return image;
        double paletteRatio = (double) paletteHeight / paletteWidth;

        BufferedImage palette = new BufferedImage(paletteWidth, paletteHeight, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = palette.createGraphics();
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, paletteWidth, paletteHeight);

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double imageRatio = (double) imageHeight / imageWidth;
        if (paletteRatio > imageRatio) {
            int centerY = paletteHeight / 2, height = (int) (paletteWidth * imageRatio);
            g2d.drawImage(image, 0, centerY - height / 2, paletteWidth, height, null);
        } else {
            int centerX = paletteWidth / 2, width = (int) (paletteHeight / imageRatio);
            g2d.drawImage(image, centerX - width / 2, 0, width, paletteHeight, null);
        }

        g2d.dispose();
        return palette;
    }

    static {
        try {
            TRANSFORMER = TransformerFactory.newInstance().newTransformer();
            DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static class BufferedImageTranscoder extends ImageTranscoder {

        private BufferedImage image;

        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            this.image = img;
        }

        public BufferedImage getBufferedImage() {
            return image;
        }
    }
}
