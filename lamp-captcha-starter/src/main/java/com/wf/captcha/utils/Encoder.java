package com.wf.captcha.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Created by 王帆 on 2018-07-27 上午 10:08.
 */
public class Encoder {
    static final int BITS = 12;
    static final int HSIZE = 5003; // 80% 占用率
    private static final int EOF = -1;
    int nBits; // number of bits/code
    int maxbits = BITS; // user settable max # bits/code
    int maxcode; // maximum code, given n_bits
    int maxmaxcode = 1 << BITS; // should NEVER generate this code
    int[] htab = new int[HSIZE];
    int[] codetab = new int[HSIZE];
    int hsize = HSIZE; // for dynamic table sizing
    int freeEnt = 0; // first unused entry
    // block compression parameters -- after all codes are used up,
    // and compression rate changes, start over.
    boolean clearFlg = false;
    int gInitBits;
    int clearCode;
    int EofCode;
    int curAccum = 0;
    int curBits = 0;

    // Algorithm:  use open addressing double hashing (no chaining) on the
    // prefix code / next character combination.  We do a variant of Knuth's
    // algorithm D (vol. 3, sec. 6.4) along with G. Knott's relatively-prime
    // secondary probe.  Here, the modular division first probe is gives way
    // to a faster exclusive-or manipulation.  Also do block compression with
    // an adaptive reset, whereby the code table is cleared when the compression
    // ratio decreases, but after the table fills.  The variable-length output
    // codes are re-sized at this point, and a special CLEAR code is generated
    // for the decompressor.  Late addition:  construct the table according to
    // file size for noticeable speed improvement on small files.  Please direct
    // questions about this implementation to ames!jaw.
    int[] masks =
            {
                    0x0000,
                    0x0001,
                    0x0003,
                    0x0007,
                    0x000F,
                    0x001F,
                    0x003F,
                    0x007F,
                    0x00FF,
                    0x01FF,
                    0x03FF,
                    0x07FF,
                    0x0FFF,
                    0x1FFF,
                    0x3FFF,
                    0x7FFF,
                    0xFFFF};
    // Number of characters so far in this 'packet'
    int a_count;
    // Define the storage for the packet accumulator
    byte[] accum = new byte[256];

    // output
    //
    // Output the given code.
    // Inputs:
    //      code:   A n_bits-bit integer.  If == -1, then EOF.  This assumes
    //              that n_bits =< wordsize - 1.
    // Outputs:
    //      Outputs code to the file.
    // Assumptions:
    //      Chars are 8 bits long.
    // Algorithm:
    //      Maintain a BITS character long buffer (so that 8 codes will
    // fit in it exactly).  Use the VAX insv instruction to insert each
    // code in turn.  When the buffer fills up empty it and start over.
    // 图片的宽高
    private int imgW, imgH;
    private byte[] pixAry;
    private int initCodeSize;  // 验证码位数
    private int remaining;  // 剩余数量
    private int curPixel;  // 像素

    //----------------------------------------------------------------------------

    /**
     * @param width       宽度
     * @param height      高度
     * @param pixels      像素
     * @param color_depth 颜色
     */
    Encoder(int width, int height, byte[] pixels, int color_depth) {
        imgW = width;
        imgH = height;
        pixAry = pixels;
        initCodeSize = Math.max(2, color_depth);
    }

    // Add a character to the end of the current packet, and if it is 254
    // characters, flush the packet to disk.

    /**
     * @param c    字节
     * @param outs 输出流
     * @throws IOException IO异常
     */
    void charOut(byte c, OutputStream outs) throws IOException {
        accum[a_count++] = c;
        if (a_count >= 254) {
            flushChar(outs);
        }
    }

    // Clear out the hash table

    // table clear for block compress

    /**
     * @param outs 输出流
     * @throws IOException IO异常
     */
    void clBlock(OutputStream outs) throws IOException {
        clHash(hsize);
        freeEnt = clearCode + 2;
        clearFlg = true;

        output(clearCode, outs);
    }

    // reset code table

    /**
     * @param hsize int
     */
    void clHash(int hsize) {
        for (int i = 0; i < hsize; ++i) {
            htab[i] = -1;
        }
    }

    /**
     * @param init_bits int
     * @param outs      输出流
     * @throws IOException IO异常
     */
    void compress(int init_bits, OutputStream outs) throws IOException {
        int fcode;
        int i /* = 0 */;
        int c;
        int ent;
        int disp;
        int hsizeReg;
        int hshift;

        // Set up the globals:  g_init_bits - initial number of bits
        gInitBits = init_bits;

        // Set up the necessary values
        clearFlg = false;
        nBits = gInitBits;
        maxcode = maxCode(nBits);

        clearCode = 1 << (init_bits - 1);
        EofCode = clearCode + 1;
        freeEnt = clearCode + 2;

        a_count = 0; // clear packet

        ent = nextPixel();

        hshift = 0;
        for (fcode = hsize; fcode < 65536; fcode *= 2) {
            ++hshift;
        }
        hshift = 8 - hshift; // set hash code range bound

        hsizeReg = hsize;
        clHash(hsizeReg); // clear hash table

        output(clearCode, outs);

        outer_loop:
        while ((c = nextPixel()) != EOF) {
            fcode = (c << maxbits) + ent;
            i = (c << hshift) ^ ent; // xor hashing

            if (htab[i] == fcode) {
                ent = codetab[i];
                continue;
            } else if (htab[i] >= 0) {
                // non-empty slot
                disp = hsizeReg - i; // secondary hash (after G. Knott)
                if (i == 0) {
                    disp = 1;
                }
                do {
                    i -= disp;
                    if (i < 0) {
                        i += hsizeReg;
                    }

                    if (htab[i] == fcode) {
                        ent = codetab[i];
                        continue outer_loop;
                    }
                } while (htab[i] >= 0);
            }
            output(ent, outs);
            ent = c;
            if (freeEnt < maxmaxcode) {
                codetab[i] = freeEnt++; // code -> hashtable
                htab[i] = fcode;
            } else {
                clBlock(outs);
            }
        }
        // Put out the final code.
        output(ent, outs);
        output(EofCode, outs);
    }

    //----------------------------------------------------------------------------

    /**
     * @param os 输出流
     * @throws IOException IO异常
     */
    void encode(OutputStream os) throws IOException {
        os.write(initCodeSize); // write "initial code size" byte

        remaining = imgW * imgH; // reset navigation variables
        curPixel = 0;

        compress(initCodeSize + 1, os); // compress and write the pixel data

        os.write(0); // write block terminator
    }

    // Flush the packet to disk, and reset the accumulator

    /**
     * @param outs 输出流
     * @throws IOException IO异常
     */
    void flushChar(OutputStream outs) throws IOException {
        if (a_count > 0) {
            outs.write(a_count);
            outs.write(accum, 0, a_count);
            a_count = 0;
        }
    }

    /**
     * @param nBits int
     * @return int
     */
    final int maxCode(int nBits) {
        return (1 << nBits) - 1;
    }

    //----------------------------------------------------------------------------
    // Return the next pixel from the image
    //----------------------------------------------------------------------------

    /**
     * @return int
     */
    private int nextPixel() {
        if (remaining == 0) {
            return EOF;
        }

        --remaining;

        byte pix = pixAry[curPixel++];

        return pix & 0xff;
    }

    /**
     * @param code int
     * @param outs 输出流
     * @throws IOException IO异常
     */
    void output(int code, OutputStream outs) throws IOException {
        curAccum &= masks[curBits];

        if (curBits > 0) {
            curAccum |= (code << curBits);
        } else {
            curAccum = code;
        }

        curBits += nBits;

        while (curBits >= 8) {
            charOut((byte) (curAccum & 0xff), outs);
            curAccum >>= 8;
            curBits -= 8;
        }

        // If the next entry is going to be too big for the code size,
        // then increase it, if possible.
        if (freeEnt > maxcode || clearFlg) {
            if (clearFlg) {
                nBits = gInitBits;
                maxcode = maxCode(nBits);
                clearFlg = false;
            } else {
                ++nBits;
                if (nBits == maxbits) {
                    maxcode = maxmaxcode;
                } else {
                    maxcode = maxCode(nBits);
                }
            }
        }

        if (code == EofCode) {
            // At EOF, write the rest of the buffer.
            while (curBits > 0) {
                charOut((byte) (curAccum & 0xff), outs);
                curAccum >>= 8;
                curBits -= 8;
            }

            flushChar(outs);
        }
    }
}
