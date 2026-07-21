package com.mndk.bteterrarenderer.mcconnector.client.input;

import java.util.HashMap;
import java.util.Map;

/**
 * Copied from 1.12.2's <code>org.lwjgl.input.Keyboard</code>,
 * from 1.18.2's <code>org.lwjgl.glfw.GLFW</code>,
 * and 26.3-snapshot-4's <code>com.mojang.blaze3d.platform.InputConstants</code>
 */
public enum InputKey {
    KEY_SPACE(44, 32, 0x39),
    KEY_APOSTROPHE(52, 39, 0x28),
    KEY_COMMA(54, 44, 0x33),
    KEY_MINUS(45, 45, 0x0C),
    KEY_PERIOD(55, 46, 0x34),
    KEY_SLASH(56, 47, 0x35),
    KEY_0(39, 48, 0x0B),
    KEY_1(30, 49, 0x02),
    KEY_2(31, 50, 0x03),
    KEY_3(32, 51, 0x04),
    KEY_4(33, 52, 0x05),
    KEY_5(34, 53, 0x06),
    KEY_6(35, 54, 0x07),
    KEY_7(36, 55, 0x08),
    KEY_8(37, 56, 0x09),
    KEY_9(38, 57, 0x0A),
    KEY_SEMICOLON(51, 59, 0x27),
    KEY_EQUAL(46, 61, 0x0D),
    KEY_A(4, 65, 0x1E),
    KEY_B(5, 66, 0x30),
    KEY_C(6, 67, 0x2E),
    KEY_D(7, 68, 0x20),
    KEY_E(8, 69, 0x12),
    KEY_F(9, 70, 0x21),
    KEY_G(10, 71, 0x22),
    KEY_H(11, 72, 0x23),
    KEY_I(12, 73, 0x17),
    KEY_J(13, 74, 0x24),
    KEY_K(14, 75, 0x25),
    KEY_L(15, 76, 0x26),
    KEY_M(16, 77, 0x32),
    KEY_N(17, 78, 0x31),
    KEY_O(18, 79, 0x18),
    KEY_P(19, 80, 0x19),
    KEY_Q(20, 81, 0x10),
    KEY_R(21, 82, 0x13),
    KEY_S(22, 83, 0x1F),
    KEY_T(23, 84, 0x14),
    KEY_U(24, 85, 0x16),
    KEY_V(25, 86, 0x2F),
    KEY_W(26, 87, 0x11),
    KEY_X(27, 88, 0x2D),
    KEY_Y(28, 89, 0x15),
    KEY_Z(29, 90, 0x2C),
    KEY_LEFT_BRACKET(47, 91, 0x1A),
    KEY_BACKSLASH(49, 92, 0x2B),
    KEY_RIGHT_BRACKET(48, 93, 0x1B),
    KEY_GRAVE_ACCENT(53, 96, 0x29),
    KEY_WORLD_1(100, 161),
    KEY_WORLD_2(50, 162),
    KEY_ESCAPE(41, 256, 0x01),
    KEY_ENTER(40, 257, 0x1C),
    KEY_TAB(43, 258, 0x0F),
    KEY_BACKSPACE(42, 259, 0x0E),
    KEY_INSERT(73, 260, 0xD2),
    KEY_DELETE(76, 261, 0xD3),
    KEY_RIGHT(79, 262, 0xCD),
    KEY_LEFT(80, 263, 0xCB),
    KEY_DOWN(81, 264, 0xD0),
    KEY_UP(82, 265, 0xC8),
    KEY_PAGE_UP(75, 266, 0xC9),
    KEY_PAGE_DOWN(78, 267, 0xD1),
    KEY_HOME(74, 268, 0xC7),
    KEY_END(77, 269, 0xCF),
    KEY_CAPS_LOCK(57, 280, 0x3A),
    KEY_SCROLL_LOCK(71, 281, 0x46),
    KEY_NUM_LOCK(83, 282, 0x45),
    KEY_PRINT_SCREEN(70, 283, 0xB7),
    KEY_PAUSE(72, 284, 0xC5),
    KEY_F1(58, 290, 0x3B),
    KEY_F2(59, 291, 0x3C),
    KEY_F3(60, 292, 0x3D),
    KEY_F4(61, 293, 0x3E),
    KEY_F5(62, 294, 0x3F),
    KEY_F6(63, 295, 0x40),
    KEY_F7(64, 296, 0x41),
    KEY_F8(65, 297, 0x42),
    KEY_F9(66, 298, 0x43),
    KEY_F10(67, 299, 0x44),
    KEY_F11(68, 300, 0x57),
    KEY_F12(69, 301, 0x58),
    KEY_F13(104, 302, 0x64),
    KEY_F14(105, 303, 0x65),
    KEY_F15(106, 304, 0x66),
    KEY_F16(107, 305, 0x67),
    KEY_F17(108, 306, 0x68),
    KEY_F18(109, 307, 0x69),
    KEY_F19(110, 308, 0x71),
    KEY_F20(111, 309),
    KEY_F21(112, 310),
    KEY_F22(113, 311),
    KEY_F23(114, 312),
    KEY_F24(115, 313),
    KEY_KP_0(98, 320, 0x52),
    KEY_KP_1(89, 321, 0x4F),
    KEY_KP_2(90, 322, 0x50),
    KEY_KP_3(91, 323, 0x51),
    KEY_KP_4(92, 324, 0x4B),
    KEY_KP_5(93, 325, 0x4C),
    KEY_KP_6(94, 326, 0x4D),
    KEY_KP_7(95, 327, 0x47),
    KEY_KP_8(96, 328, 0x48),
    KEY_KP_9(97, 329, 0x49),
    KEY_KP_DECIMAL(220, 330, 0x53),
    KEY_KP_DIVIDE(84, 331, 0xB5),
    KEY_KP_MULTIPLY(85, 332, 0x37),
    KEY_KP_SUBTRACT(86, 333, 0x4A),
    KEY_KP_ADD(87, 334, 0x4E),
    KEY_KP_ENTER(88, 335, 0x9C),
    KEY_KP_EQUAL(103, 336, 0x8D),
    KEY_LEFT_SHIFT(225, 340, 0x2A),
    KEY_LEFT_CONTROL(224, 341, 0x1D),
    KEY_LEFT_ALT(226, 342, 0x38),
    KEY_LEFT_SUPER(226, 343, 0xC4), // Left super and right super are the same in keycode
    KEY_RIGHT_SHIFT(229, 344, 0x36),
    KEY_RIGHT_CONTROL(228, 345, 0x9D),
    KEY_RIGHT_ALT(230, 346, 0xB8),
    KEY_RIGHT_SUPER(230, 347, 0xC4), // Left super and right super are the same in keycode
    KEY_MENU(118, 348);

    public final int sdlKeyCode;
    public final int glfwKeyCode;
    public final int keyboardCode;

    private static final Map<Integer, InputKey> SDL_KEYCODE_MAP = new HashMap<>();
    private static final Map<Integer, InputKey> GLFW_KEYCODE_MAP = new HashMap<>();
    private static final Map<Integer, InputKey> KEYCODE_MAP = new HashMap<>();

    InputKey(int sdlKeyCode, int glfwKeyCode, int keyboardCode) {
        this.sdlKeyCode = sdlKeyCode;
        this.glfwKeyCode = glfwKeyCode;
        this.keyboardCode = keyboardCode;
    }

    InputKey(int sdlKeyCode, int glfwKeyCode) {
        this.sdlKeyCode = sdlKeyCode;
        this.glfwKeyCode = glfwKeyCode;
        this.keyboardCode = -1;
    }

    /**
     * This method is for version-specific impl classes
     */
    public static InputKey fromSdlKeyCode(int sdlKeyCode) {
        return SDL_KEYCODE_MAP.get(sdlKeyCode);
    }

    /**
     * This method is for version-specific impl classes
     */
    public static InputKey fromGlfwKeyCode(int glfwKeyCode) {
        return GLFW_KEYCODE_MAP.get(glfwKeyCode);
    }

    /**
     * This method is for version-specific impl classes
     */
    public static InputKey fromKeyboardCode(int keyCode) {
        return KEYCODE_MAP.get(keyCode);
    }

    static {
        for (InputKey key : values()) {
            SDL_KEYCODE_MAP.put(key.sdlKeyCode, key);
            GLFW_KEYCODE_MAP.put(key.glfwKeyCode, key);
            KEYCODE_MAP.put(key.keyboardCode, key);
        }
    }
}
