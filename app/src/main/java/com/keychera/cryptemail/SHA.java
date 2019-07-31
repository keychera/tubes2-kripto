package com.keychera.cryptemail;


import java.math.BigInteger;

/**
 * sha
 */

public class SHA {
  static long h0;
  static long h1;
  static long h2;
  static long h3;
  static long h4;

  public static String SHA1(String message) {

    h0 = 0x67452301L;
    h1 = 0xEFCDAB89L;
    h2 = 0x98BADCFEL;
    h3 = 0x10325476L;
    h4 = 0xC3D2E1F0L;

    String preprocessed = Preprocess(message);

    for (int i = 0; i < preprocessed.length() / 512; i++) {
      ProcessChunk(preprocessed.substring(i * 512, (i + 1) * 512));
    }

    BigInteger h0_s = BigInteger.valueOf(h0).shiftLeft(128);
    BigInteger h1_s = BigInteger.valueOf(h1).shiftLeft(96);
    BigInteger h2_s = BigInteger.valueOf(h2).shiftLeft(64);
    BigInteger h3_s = BigInteger.valueOf(h3).shiftLeft(32);
    BigInteger h4_s = BigInteger.valueOf(h4);

    BigInteger hh = h4_s.or(h3_s.or(h2_s.or(h1_s.or(h0_s))));
    return hh.toString(16);
  };

  private static String Preprocess(String message) {
    String messageBit = GetBinaryString(message);

    if (message.length() > 0) {
      messageBit = new BigInteger(message.getBytes()).toString(2);
    }
    String processedString = messageBit + "1";
    while ((processedString.length() + 64) % 512 != 0) {
      processedString += "0";
    }

    String ml = Integer.toBinaryString(message.length() * 8);
    for (int i = ml.length(); i < 64; i++) {
      ml = "0" + ml;
    }
    return processedString + ml;
  };

  private static void ProcessChunk(String chunk) {
    long w[] = new long[80];
    int bitLength = 32;

    for (int i = 0; i < 16; i++) {
      w[i] = Long.parseLong(chunk.substring(i * bitLength, (i + 1) * bitLength), 2);
    }

    for (int i = 16; i < 80; i++) {
      w[i] = LeftRotate(w[i-3] ^ w[i-8] ^ w[i-14] ^ w[i-16], 1 ,bitLength);
    }

    long a = h0;
    long b = h1;
    long c = h2;
    long d = h3;
    long e = h4;
    long f, k;

    for (int i = 0; i < 80; i++) {
      if (i >= 0 && i <= 19) {
        f = (b & c) | (~b & d);
        k = 0x5A827999L;
      } else if (i >= 20 && i <= 39) {
        f = b ^ c ^ d;
        k = 0x6ED9EBA1L;
      } else if (i >= 40 && i <= 59) {
        f = (b & c) | (b & d) | (c & d);
        k = 0x8F1BBCDCL;
      } else {
        f = b ^ c ^ d;
        k = 0xCA62C1D6L;
      }

      long temp = (LeftRotate(a, 5, 32) + f + e + k + w[i]) % (1L << (bitLength));
      e = d;
      d = c;
      c = LeftRotate(b, 30, bitLength);
      b = a;
      a = temp;

    }

    h0 = (h0 + a) % (1L << (bitLength));
    h1 = (h1 + b) % (1L << (bitLength));
    h2 = (h2 + c) % (1L << (bitLength));
    h3 = (h3 + d) % (1L << (bitLength));
    h4 = (h4 + e) % (1L << (bitLength));

  };

  private static String GetBinaryString(String s) {
    byte[] bytes = s.getBytes();
    StringBuilder binary = new StringBuilder();
    for (byte b : bytes)
    {
      int val = b;
      for (int i = 0; i < 8; i++)
      {
        binary.append((val & 128) == 0 ? 0 : 1);
        val <<= 1;
      }
    }
    return binary.toString();
  };

  public static long LeftRotate(long inp, int rotation, int bitLength) {
    long res = inp;
    long msb;
    for (int i = 0; i < rotation; i++) {
      msb = (res & (1L << (bitLength - 1))) >>> (bitLength - 1);
      res = (res << 1) % (1L << (bitLength));
      res += msb;
      res = res % (1L << (bitLength));
    }
    return res;
  };
}

