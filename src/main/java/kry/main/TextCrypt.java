package kry.main;

import kry.spn.SPN;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class TextCrypt {

    SPN spn;

    public TextCrypt(SPN spn) {
        this.spn = spn;
    }

    public String encryptText(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);

        StringBuilder bitStringBuilder = new StringBuilder(bytes.length * 8);
        for (byte oneByte :
                bytes) {
            bitStringBuilder.append(String.format("%8s", Integer.toBinaryString(oneByte)).replace(' ', '0'));
        }

        bitStringBuilder.append("1");

        while (bitStringBuilder.length() % 16 != 0) {
            bitStringBuilder.append("0");
        }

        int y = ThreadLocalRandom.current().nextInt(0, 1 << spn.getMaxBits());

        StringBuilder chiffreText = new StringBuilder();
        chiffreText.append(String.format("%16s", Integer.toBinaryString(y)).replace(' ', '0'));

        for (int i = 0; i < bitStringBuilder.length() / 16; i++) {
            int startIndex = i * 16;
            String subString = bitStringBuilder.substring(startIndex, startIndex + 16);
            int integer = Integer.parseInt(subString, 2);

            int yToEncrypt = (y + i) % (1 << spn.getMaxBits());

            int yEncrypted = spn.encryptInt(yToEncrypt);
            int outputNumber = yEncrypted ^ integer;
            chiffreText.append(String.format("%16s", Integer.toBinaryString(outputNumber)).replace(' ', '0'));
        }

        System.out.println(bitStringBuilder);
        System.out.println(chiffreText);
        return chiffreText.toString();
    }

    public String decryptText(String bitString) {

        String ySubString = bitString.substring(0, 16);
        int y = Integer.parseInt(ySubString, 2);

        StringBuilder clearStringbuilder = new StringBuilder();

        for (int i = 1; i < bitString.length() / 16; i++) {
            int startIndex = i * 16;
            String subString = bitString.substring(startIndex, startIndex + 16);
            int integer = Integer.parseInt(subString, 2);

            int yToEncrypt = (y + i - 1) % (1 << spn.getMaxBits());

            int yEncrypted = spn.encryptInt(yToEncrypt);
            int outputNumber = yEncrypted ^ integer;
            clearStringbuilder.append(String.format("%16s", Integer.toBinaryString(outputNumber)).replace(' ', '0'));
        }

        String str = clearStringbuilder.toString().replaceFirst("10*$", "");

        byte[] bval = new BigInteger(str, 2).toByteArray();

        return new String(bval, StandardCharsets.US_ASCII);
    }


}
