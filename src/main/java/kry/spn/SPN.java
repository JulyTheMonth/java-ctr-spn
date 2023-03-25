package kry.spn;

public class SPN {
    int r = 4;
    int n = 4;
    int m = 4;
    int nMask;
    int[] sBox = {
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7
    };

    int[] sBoxInverse;

    int[] bitPermutation = {
            0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15
    };

    int key = 0b0011_1010_1001_0100_1101_0110_0011_1111;
//    int key = 0b0001_0001_0010_1000_1000_1100_0000_0000;


    public SPN() {
        nMask = 0;

        //Creates a mask for the bit permutation
        for (int i = 0; i < n; i++) {
            nMask = nMask << 1 | 1;
        }

        sBoxInverse = new int[n * m];

        for (int i = 0; i < sBox.length; i++) {
            sBoxInverse[sBox[i]] = i;
        }

    }

    public int getMaxBits(){
        return n*m;
    }

    public int getRoundKey(int i) {
        int keysearch = 0b1111_1111_1111_1111 << (4 * (4 - i));

        return (keysearch & key) >> (4 * (4 - i));
    }

    public int getInverseRoundKey(int i) {
        if (i == 0 || i == this.r) {
            return getRoundKey(this.r - i);
        } else {
            return bitPermutation(getRoundKey(this.r - i));
        }
    }


    public int encryptInt(int clear) {
        int encrypt = clear;
        //1: r 0 XOr
        encrypt = encrypt ^ getRoundKey(0);
        //2: r-1 runden
        for (int i = 1; i < r; i++) {
            //Round r
            //S-BOx
            encrypt = wordSubstitution(encrypt, sBox);
            //Bitpermutation
            encrypt = bitPermutation(encrypt);

            //Xor
            encrypt = encrypt ^ getRoundKey(i);
        }

        //3: last round
        //SBox
        encrypt = wordSubstitution(encrypt, sBox);
        //XOr
        encrypt = encrypt ^ getRoundKey(r);
        return encrypt;

    }


    public int decryptInt(int clear) {
        int encrypt = clear;
        //1: r 0 XOr
        encrypt = encrypt ^ getInverseRoundKey(0);
        //2: r-1 runden
        for (int i = 1; i < r; i++) {
            //Round r
            //S-BOx
            encrypt = wordSubstitution(encrypt, sBoxInverse);
            //Bitpermutation
            encrypt = bitPermutation(encrypt);
            //Xor
            encrypt = encrypt ^ getInverseRoundKey(i);
        }

        //3: last round
        //SBox
        encrypt = wordSubstitution(encrypt, sBoxInverse);
        //XOr
        encrypt = encrypt ^ getInverseRoundKey(r);
        return encrypt;

    }


    private int bitPermutation(int encrypt) {
        int encryptTemp = 0;
        for (int j = 1; j <= n * m; j++) {
            int currentBitMask = 1 << (n * m) - j;

            int currentBit = encrypt & currentBitMask;

            currentBit >>= (n * m) - j;
            currentBit <<= (n * m) - bitPermutation[j - 1] - 1;
            encryptTemp = encryptTemp | currentBit;

        }

        encrypt = encryptTemp;
        return encrypt;
    }

    public int wordSubstitution(int word, int[] usedSbox) {
        for (int j = 0; j < m; j++) {

            //Calcs the current shift.
            int currentShift = (n * m) - n * (m - j);
            //Current Mask for currently edited bits.
            int cNMask = (nMask << currentShift);
            //Extracted bits
            int bits = (word & cNMask);
            //Back shift to access sBox.
            bits >>= currentShift;
            //delete bits of the mask
            word = word & ~cNMask;
            //Copies new bits to the number
            word |= (usedSbox[bits] << currentShift);

        }
        return word;

    }


    public String getIntString(int i) {
        return String.format("%" + (n * m) + "s", Integer.toBinaryString(i)).replace(' ', '0');
    }


    public void printInt(int i) {
        String intString = getIntString(i);
        String[] stringArray = intString.split("(?<=\\G" + ".".repeat(n) + ")");
        StringBuilder sb = new StringBuilder();
        for (String str :
                stringArray) {
            sb.append(str).append("_");
        }

        System.out.println(sb.toString());
    }


}
