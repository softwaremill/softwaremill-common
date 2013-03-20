package com.softwaremill.common.util.stringsorting;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StringToByteSortableRepresentationConverter {
    private final String wrapped;

    public StringToByteSortableRepresentationConverter(String wrapped) {
        this.wrapped = wrapped;
    }

    public String convertWithLowercase() {
        char[] in = wrapped.toLowerCase().toCharArray();
		char[] out = new char[in.length];

		int i = 0;
		for (char c : in) {
            char replacement = CONVERSION[(int) c];
            if (replacement != 0) {
                out[i] = replacement;
            } else {
                out[i] = ' '; // Unknown characters come first
            }

            i++;
		}

		return new String(out);
    }

    private final static char[] CONVERSION = new char[381];

    static {
        // Generated using SortedCharsToConsecutiveCharsAssigner
        CONVERSION[33] = ' '; // ! = 32
        CONVERSION[64] = '!'; // @ = 33
        CONVERSION[35] = '"'; // # = 34
        CONVERSION[36] = '#'; // $ = 35
        CONVERSION[37] = '$'; // % = 36
        CONVERSION[94] = '%'; // ^ = 37
        CONVERSION[38] = '&'; // & = 38
        CONVERSION[42] = '\''; // * = 39
        CONVERSION[40] = '('; // ( = 40
        CONVERSION[41] = ')'; // ) = 41
        CONVERSION[95] = '*'; // _ = 42
        CONVERSION[43] = '+'; // + = 43
        CONVERSION[45] = ','; // - = 44
        CONVERSION[61] = '-'; // = = 45
        CONVERSION[123] = '.'; // { = 46
        CONVERSION[125] = '/'; // } = 47
        CONVERSION[91] = '0'; // [ = 48
        CONVERSION[93] = '1'; // ] = 49
        CONVERSION[58] = '2'; // : = 50
        CONVERSION[124] = '3'; // | = 51
        CONVERSION[34] = '4'; // " = 52
        CONVERSION[59] = '5'; // ; = 53
        CONVERSION[39] = '6'; // ' = 54
        CONVERSION[92] = '7'; // \ = 55
        CONVERSION[44] = '8'; // , = 56
        CONVERSION[46] = '9'; // . = 57
        CONVERSION[47] = ':'; // / = 58
        CONVERSION[60] = ';'; // < = 59
        CONVERSION[62] = '<'; // > = 60
        CONVERSION[63] = '='; // ? = 61
        CONVERSION[126] = '>'; // ~ = 62
        CONVERSION[96] = '?'; // ` = 63
        CONVERSION[48] = '@'; // 0 = 64
        CONVERSION[49] = 'A'; // 1 = 65
        CONVERSION[50] = 'B'; // 2 = 66
        CONVERSION[51] = 'C'; // 3 = 67
        CONVERSION[52] = 'D'; // 4 = 68
        CONVERSION[53] = 'E'; // 5 = 69
        CONVERSION[54] = 'F'; // 6 = 70
        CONVERSION[55] = 'G'; // 7 = 71
        CONVERSION[56] = 'H'; // 8 = 72
        CONVERSION[57] = 'I'; // 9 = 73
        CONVERSION[97] = 'J'; // a = 74
        CONVERSION[261] = 'K'; // ą = 75
        CONVERSION[228] = 'L'; // ä = 76
        CONVERSION[98] = 'M'; // b = 77
        CONVERSION[99] = 'N'; // c = 78
        CONVERSION[263] = 'O'; // ć = 79
        CONVERSION[100] = 'P'; // d = 80
        CONVERSION[101] = 'Q'; // e = 81
        CONVERSION[281] = 'R'; // ę = 82
        CONVERSION[102] = 'S'; // f = 83
        CONVERSION[103] = 'T'; // g = 84
        CONVERSION[104] = 'U'; // h = 85
        CONVERSION[105] = 'V'; // i = 86
        CONVERSION[106] = 'W'; // j = 87
        CONVERSION[107] = 'X'; // k = 88
        CONVERSION[108] = 'Y'; // l = 89
        CONVERSION[322] = 'Z'; // ł = 90
        CONVERSION[109] = '['; // m = 91
        CONVERSION[110] = '\\'; // n = 92
        CONVERSION[324] = ']'; // ń = 93
        CONVERSION[111] = '^'; // o = 94
        CONVERSION[243] = '_'; // ó = 95
        CONVERSION[246] = '`'; // ö = 96
        CONVERSION[112] = 'a'; // p = 97
        CONVERSION[113] = 'b'; // q = 98
        CONVERSION[114] = 'c'; // r = 99
        CONVERSION[115] = 'd'; // s = 100
        CONVERSION[347] = 'e'; // ś = 101
        CONVERSION[223] = 'f'; // ß = 102
        CONVERSION[116] = 'g'; // t = 103
        CONVERSION[117] = 'h'; // u = 104
        CONVERSION[252] = 'i'; // ü = 105
        CONVERSION[118] = 'j'; // v = 106
        CONVERSION[119] = 'k'; // w = 107
        CONVERSION[120] = 'l'; // x = 108
        CONVERSION[121] = 'm'; // y = 109
        CONVERSION[122] = 'n'; // z = 110
        CONVERSION[380] = 'o'; // ż = 111
        CONVERSION[378] = 'p'; // ź = 112
    }
}
