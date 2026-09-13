/*
 * Copyright (C) 2024 The Draco Authors (for providing the original C++ code)
 * Copyright (C) 2024 m4ndeokyi (for translating the code into Java)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mndk.bteterrarenderer.draco.io;

import com.mndk.bteterrarenderer.datatype.DataType;
import com.mndk.bteterrarenderer.datatype.number.UInt;
import com.mndk.bteterrarenderer.draco.attributes.AttributeValueIndex;
import com.mndk.bteterrarenderer.draco.attributes.GeometryAttribute;
import com.mndk.bteterrarenderer.draco.attributes.PointAttribute;
import com.mndk.bteterrarenderer.draco.attributes.PointIndex;
import com.mndk.bteterrarenderer.draco.core.Status;
import com.mndk.bteterrarenderer.draco.core.StatusAssert;
import com.mndk.bteterrarenderer.draco.mesh.Mesh;
import com.mndk.bteterrarenderer.draco.metadata.AttributeMetadata;
import com.mndk.bteterrarenderer.draco.metadata.Metadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class ObjectDecoderTest {

    @Test
    public void extraVertexObj() {
        DracoTestFileUtil.testDecoding("draco/testdata/extra_vertex.obj");
    }

    @Test
    public void partialAttributesObj() {
        DracoTestFileUtil.testDecoding("draco/testdata/cube_att_partial.obj");
    }

    @Test
    public void subObjects() {
        File file = DracoTestFileUtil.toFile("draco/testdata/cube_att_sub_o.obj");
        Mesh mesh = MeshIOUtil.decode(file).getValueOr(Status::throwException);
        Assertions.assertTrue(mesh.getNumFaces() > 0);
        Assertions.assertEquals(4, mesh.getNumAttributes());
        Assertions.assertEquals(GeometryAttribute.Type.GENERIC, mesh.getAttribute(3).getAttributeType());
        Assertions.assertEquals(3, mesh.getAttribute(3).size());
        Assertions.assertEquals(UInt.of(3), mesh.getAttribute(3).getUniqueId());
    }

    @Test
    public void subObjectsWithMetadata() {
        File file = DracoTestFileUtil.toFile("draco/testdata/cube_att_sub_o.obj");
        Mesh mesh = MeshIOUtil.decodeWithMetadata(file).getValueOr(Status::throwException);
        Assertions.assertTrue(mesh.getNumFaces() > 0);

        Assertions.assertEquals(4, mesh.getNumAttributes());
        Assertions.assertEquals(GeometryAttribute.Type.GENERIC, mesh.getAttribute(3).getAttributeType());
        Assertions.assertEquals(3, mesh.getAttribute(3).size());

        Assertions.assertNotNull(mesh.getMetadata());
        AttributeMetadata attributeMetadata = mesh.getAttributeMetadataByAttributeId(3);
        Assertions.assertNotNull(attributeMetadata);
        AtomicReference<Integer> subObjectId = new AtomicReference<>();
        StatusAssert.assertOk(attributeMetadata.getEntryInt("obj2", subObjectId::set));
        Assertions.assertEquals(2, subObjectId.get().intValue());
    }

    @Test
    public void quadTriangulateObj() {
        File file = DracoTestFileUtil.toFile("draco/testdata/cube_quads.obj");
        Mesh mesh = MeshIOUtil.decode(file).getValueOr(Status::throwException);
        Assertions.assertEquals(12, mesh.getNumFaces());
        Assertions.assertEquals(3, mesh.getNumAttributes());
        Assertions.assertEquals(4 * 6, mesh.getNumPoints());
    }

    @Test
    public void quadPreserveObj() {
        File file = DracoTestFileUtil.toFile("draco/testdata/cube_quads.obj");
        Mesh mesh = MeshIOUtil.decodeWithPolygons(file).getValueOr(Status::throwException);
        Assertions.assertEquals(12, mesh.getNumFaces());

        Assertions.assertEquals(4, mesh.getNumAttributes());
        Assertions.assertEquals(4 * 6, mesh.getNumPoints());

        Assertions.assertEquals(GeometryAttribute.Type.GENERIC, mesh.getAttribute(3).getAttributeType());

        PointAttribute attribute = mesh.getAttribute(3);
        Assertions.assertEquals(2, attribute.size());
        Assertions.assertEquals(0, attribute.getValue(AttributeValueIndex.of(0), DataType.uint8(), 1).get().intValue());
        Assertions.assertEquals(1, attribute.getValue(AttributeValueIndex.of(1), DataType.uint8(), 1).get().intValue());
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of((4 * i))).getValue());
            Assertions.assertEquals(1, attribute.getMappedIndex(PointIndex.of((4 * i + 1))).getValue());
            Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of((4 * i + 2))).getValue());
            Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of((4 * i + 3))).getValue());
        }

        Metadata metadata = mesh.getAttributeMetadataByAttributeId(3);
        Assertions.assertNotNull(metadata);
        Assertions.assertEquals(1, metadata.getEntries().size());
        StringBuilder name = new StringBuilder();
        StatusAssert.assertOk(metadata.getEntryString("name", name));
        Assertions.assertEquals("added_edges", name.toString());
    }

    @Test
    public void octagonTriangulatedObj() {
        File file = DracoTestFileUtil.toFile("draco/testdata/octagon.obj");
        Mesh mesh = MeshIOUtil.decode(file).getValueOr(Status::throwException);
        Assertions.assertEquals(1, mesh.getNumAttributes());
        Assertions.assertEquals(8, mesh.getNumPoints());
        Assertions.assertEquals(GeometryAttribute.Type.POSITION, mesh.getAttribute(0).getAttributeType());
        Assertions.assertEquals(8, mesh.getAttribute(0).size());
    }

    @Test
    public void octagonPreservedObj() {
        File file = DracoTestFileUtil.toFile("draco/testdata/octagon.obj");
        Mesh mesh = MeshIOUtil.decodeWithPolygons(file).getValueOr(Status::throwException);
        Assertions.assertEquals(2, mesh.getNumAttributes());

        PointAttribute attribute = mesh.getAttribute(0);
        Assertions.assertEquals(GeometryAttribute.Type.POSITION, attribute.getAttributeType());
        Assertions.assertEquals(8, attribute.size());

        // Expect a new generic attribute.
        attribute = mesh.getAttribute(1);
        Assertions.assertEquals(GeometryAttribute.Type.GENERIC, attribute.getAttributeType());

        // There are four vertices with both old and new edges in their ring.
        Assertions.assertEquals(8 + 4, mesh.getNumPoints());

        Assertions.assertEquals(2, attribute.size());
        Assertions.assertEquals(0, attribute.getValue(AttributeValueIndex.of(0), DataType.uint8(), 1).get().intValue());
        Assertions.assertEquals(1, attribute.getValue(AttributeValueIndex.of(1), DataType.uint8(), 1).get().intValue());

        // 0: Old edge, 1: New edge
        Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of(0)).getValue());
        Assertions.assertEquals(1, attribute.getMappedIndex(PointIndex.of(1)).getValue());
        Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of(2)).getValue());
        Assertions.assertEquals(1, attribute.getMappedIndex(PointIndex.of(3)).getValue());
        Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of(4)).getValue());
        Assertions.assertEquals(1, attribute.getMappedIndex(PointIndex.of(5)).getValue());
        Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of(6)).getValue());
        Assertions.assertEquals(1, attribute.getMappedIndex(PointIndex.of(7)).getValue());
        Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of(8)).getValue());
        Assertions.assertEquals(1, attribute.getMappedIndex(PointIndex.of(9)).getValue());
        Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of(10)).getValue());
        Assertions.assertEquals(0, attribute.getMappedIndex(PointIndex.of(11)).getValue());

        Metadata metadata = mesh.getAttributeMetadataByAttributeId(1);
        Assertions.assertNotNull(metadata);
        Assertions.assertEquals(1, metadata.getEntries().size());
        StringBuilder name = new StringBuilder();
        StatusAssert.assertOk(metadata.getEntryString("name", name));
        Assertions.assertEquals("added_edges", name.toString());
    }

    @Test
    public void emptyNameObj() {
        File file = DracoTestFileUtil.toFile("draco/testdata/empty_name.obj");
        Mesh mesh = MeshIOUtil.decode(file).getValueOr(Status::throwException);
        Assertions.assertEquals(1, mesh.getNumAttributes());
        Assertions.assertEquals(3, mesh.getAttribute(0).size());
    }

    @Test
    public void pointCloudObj() {
        File file = DracoTestFileUtil.toFile("draco/testdata/test_lines.obj");
        Mesh mesh = MeshIOUtil.decode(file, false).getValueOr(Status::throwException);
        Assertions.assertEquals(0, mesh.getNumFaces());
        Assertions.assertEquals(1, mesh.getNumAttributes());
        Assertions.assertEquals(484, mesh.getAttribute(0).size());
    }

    @Test
    public void wrongAttributeMapping() {
        File file = DracoTestFileUtil.toFile("draco/testdata/test_wrong_attribute_mapping.obj");
        Mesh mesh = MeshIOUtil.decode(file).getValueOr(Status::throwException);
        Assertions.assertEquals(1, mesh.getNumAttributes());
        Assertions.assertEquals(3, mesh.getAttribute(0).size());
    }

    @Test
    public void testObjDecodingAll() {
        DracoTestFileUtil.testDecoding("draco/testdata/bunny_norm.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/cube_att.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/cube_att_partial.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/cube_att_sub_o.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/cube_quads.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/cube_subd.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/eof_test.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/extra_vertex.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/mat_test.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/one_face_123.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/one_face_312.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/one_face_321.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/sphere.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/test_nm.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/test_nm_trans.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/test_sphere.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/three_faces_123.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/three_faces_312.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/two_faces_123.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/two_faces_312.obj");
        DracoTestFileUtil.testDecoding("draco/testdata/inf_nan.obj");
    }
}
