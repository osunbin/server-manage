package com.bin.sm.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

import static java.lang.Math.abs;

public class HashUtil {

    private static final int MURMUR32_BLOCK_SIZE = 4;
    private static final int MURMUR64_BLOCK_SIZE = 16;
    private static final int DEFAULT_MURMUR_SEED = 0x01000193;
    
    public static int hashToIndex(int hash, int length) {
        Preconditions.checkPositive("length", length);

        if (hash == Integer.MIN_VALUE) {
            return 0;
        }

        return abs(hash) % length;
    }


    /**
     * MurMurHash算法, 性能高, 碰撞率低
     * @param str String
     * @return Long
     */
    public static int hash(String str) {
        HashFunction hashFunction = Hashing.murmur3_128();
        return hashFunction.hashString(str, StandardCharsets.UTF_8).asInt();
    }

    public static int hash(int seed,String str) {
        HashFunction hashFunction = Hashing.murmur3_128(seed);
        return hashFunction.hashString(str, StandardCharsets.UTF_8).asInt();
    }

    /**
     * Long转换成无符号长整型（C中数据类型）
     * Java的数据类型long与C语言中无符号长整型uint64_t有区别，导致Java输出版本存在负数
     * @param value long
     * @return Long
     */
    public static Long readUnsignedLong(long value) {
        if (value >= 0){
            return value;
        }
        return value & Long.MAX_VALUE;
    }

    /**
     * 返回无符号murmur hash值
     * @param key
     * @return
     */
    public static Long hashUnsigned(String key) {
        return readUnsignedLong(hash(key));
    }

    public static int intHash(final int value, final int mask) {
        return fastIntMix(value) & mask;
    }

    public static int longHash(final long value, final int mask) {
        return ((int) fastLongMix(value)) & mask;
    }

    public static int evenLongHash(final long value, final int mask) {
        final int h = (int) fastLongMix(value);
        return h & mask & ~1;
    }

    public static int hash(Object value, int mask) {
        return fastIntMix(value.hashCode()) & mask;
    }

    public  static int hashCode(long value) {
        // Used only for nominal Object.hashCode implementations, no mixing
        // required.
        return (int) (value ^ (value >>> Integer.SIZE));
    }

    public static int MurmurHash3_x86_32(String key) {
        return MurmurHash3_x86_32(key, DEFAULT_MURMUR_SEED);
    }

    public static int MurmurHash3_x86_32(String key,int seed) {
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        return MurmurHash3_x86_32(bytes, 0, bytes.length, seed);
    }


    public static int MurmurHash3_x86_32(byte[] data, int offset, int len) {
        return MurmurHash3_x86_32(data, offset, len, DEFAULT_MURMUR_SEED);
    }

    public static  int MurmurHash3_x86_32(byte[]  resource, long offset, int len, int seed) {
        // (len & ~(MURMUR32_BLOCK_SIZE - 1)) is the length rounded down to the Murmur32 block size boundary
        final long tailStart = offset + (len & ~(MURMUR32_BLOCK_SIZE - 1));

        int c1 = 0xcc9e2d51;
        int c2 = 0x1b873593;

        int h1 = seed;

        for (long blockAddr = offset; blockAddr < tailStart; blockAddr += MURMUR32_BLOCK_SIZE) {
            // little-endian load order
            int k1 = getInt(resource, blockAddr);
            k1 *= c1;
            // ROTL32(k1,15);
            k1 = (k1 << 15) | (k1 >>> 17);
            k1 *= c2;

            h1 ^= k1;
            // ROTL32(h1,13);
            h1 = (h1 << 13) | (h1 >>> 19);
            h1 = h1 * 5 + 0xe6546b64;
        }

        // tail
        int k1 = 0;

        switch (len & 0x03) {
            case 3:
                k1 = (getByte(resource, tailStart + 2) & 0xff) << 16;
                // fallthrough
            case 2:
                k1 |= (getByte(resource, tailStart + 1) & 0xff) << 8;
                // fallthrough
            case 1:
                k1 |= getByte(resource, tailStart) & 0xff;
                k1 *= c1;
                // ROTL32(k1,15);
                k1 = (k1 << 15) | (k1 >>> 17);
                k1 *= c2;
                h1 ^= k1;
            default:
        }

        // finalization
        h1 ^= len;
        h1 = MurmurHash3_fmix(h1);
        return h1;
    }


    public static long MurmurHash3_x64_64(String key) {
        return MurmurHash3_x64_64(key,DEFAULT_MURMUR_SEED);
    }

    public static long MurmurHash3_x64_64(String key,final int seed) {
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        return MurmurHash3_x64_64(bytes, 0, bytes.length,seed);
    }

    public static long MurmurHash3_x64_64(byte[] data, int offset, int len) {
        return MurmurHash3_x64_64(data, offset, len, DEFAULT_MURMUR_SEED);
    }

    public static  long MurmurHash3_x64_64(byte[] resource, long offset, int len, final int seed) {

        // (len & ~(MURMUR64_BLOCK_SIZE - 1)) is the length rounded down to the Murmur64 block boundary
        final long tailStart = offset + (len & ~(MURMUR64_BLOCK_SIZE - 1));

        long h1 = 0x9368e53c2f6af274L ^ seed;
        long h2 = 0x586dcd208f7cd3fdL ^ seed;

        long c1 = 0x87c37b91114253d5L;
        long c2 = 0x4cf5ad432745937fL;

        long k1;
        long k2;

        for (long blockAddr = offset; blockAddr < tailStart; blockAddr += MURMUR64_BLOCK_SIZE) {
            k1 = getLong(resource, blockAddr);
            k2 = getLong(resource, blockAddr + 8);
            // bmix(state);
            k1 *= c1;
            k1 = (k1 << 23) | (k1 >>> 64 - 23);
            k1 *= c2;
            h1 ^= k1;
            h1 += h2;

            h2 = (h2 << 41) | (h2 >>> 64 - 41);

            k2 *= c2;
            k2 = (k2 << 23) | (k2 >>> 64 - 23);
            k2 *= c1;
            h2 ^= k2;
            h2 += h1;

            h1 = h1 * 3 + 0x52dce729;
            h2 = h2 * 3 + 0x38495ab5;

            c1 = c1 * 5 + 0x7b7d159c;
            c2 = c2 * 5 + 0x6bce6396;
        }

        k1 = 0;
        k2 = 0;

        switch (len & 15) {
            case 15:
                k2 ^= (long) getByte(resource, tailStart + 14) << 48;
            case 14:
                k2 ^= (long) getByte(resource, tailStart + 13) << 40;
            case 13:
                k2 ^= (long) getByte(resource, tailStart + 12) << 32;
            case 12:
                k2 ^= (long) getByte(resource, tailStart + 11) << 24;
            case 11:
                k2 ^= (long) getByte(resource, tailStart + 10) << 16;
            case 10:
                k2 ^= (long) getByte(resource, tailStart + 9) << 8;
            case 9:
                k2 ^= getByte(resource, tailStart + 8);

            case 8:
                k1 ^= (long) getByte(resource, tailStart + 7) << 56;
            case 7:
                k1 ^= (long) getByte(resource, tailStart + 6) << 48;
            case 6:
                k1 ^= (long) getByte(resource, tailStart + 5) << 40;
            case 5:
                k1 ^= (long) getByte(resource, tailStart + 4) << 32;
            case 4:
                k1 ^= (long) getByte(resource, tailStart + 3) << 24;
            case 3:
                k1 ^= (long) getByte(resource, tailStart + 2) << 16;
            case 2:
                k1 ^= (long) getByte(resource, tailStart + 1) << 8;
            case 1:
                k1 ^= getByte(resource, tailStart);

                // bmix();
                k1 *= c1;
                k1 = (k1 << 23) | (k1 >>> 64 - 23);
                k1 *= c2;
                h1 ^= k1;
                h1 += h2;

                h2 = (h2 << 41) | (h2 >>> 64 - 41);

                k2 *= c2;
                k2 = (k2 << 23) | (k2 >>> 64 - 23);
                k2 *= c1;
                h2 ^= k2;
                h2 += h1;

                h1 = h1 * 3 + 0x52dce729;
                h2 = h2 * 3 + 0x38495ab5;
            default:
        }

        h2 ^= len;

        h1 += h2;
        h2 += h1;

        h1 = MurmurHash3_fmix(h1);
        h2 = MurmurHash3_fmix(h2);

        return h1 + h2;
    }

    public static int MurmurHash3_fmix(int k) {
        k ^= k >>> 16;
        k *= 0x85ebca6b;
        k ^= k >>> 13;
        k *= 0xc2b2ae35;
        k ^= k >>> 16;
        return k;
    }

    public static long MurmurHash3_fmix(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;
        return k;
    }

    static long fastLongMix(long k) {
        // phi = 2^64 / goldenRatio
        final long phi = 0x9E3779B97F4A7C15L;
        long h = k * phi;
        h ^= h >>> 32;
        return h ^ (h >>> 16);
    }

    static int fastIntMix(int k) {
        // phi = 2^32 / goldenRatio
        final int phi = 0x9E3779B9;
        final int h = k * phi;
        return h ^ (h >>> 16);
    }




    public static int getInt(byte[] buf, long offset) {
        return readIntL( buf, offset);
    }


    public static long getLong(byte[] buf, long offset) {
        return readLongL(buf, offset);
    }


    public static byte getByte(byte[] buf, long offset) {
        return buf[(int) offset];
    }

    private static  int readIntL(byte[] resource, long offset) {
        int byte3 = getByte(resource, offset) & 0xFF;
        int byte2 = (getByte(resource, offset + 1) & 0xFF) << 8;
        int byte1 = (getByte(resource, offset + 2) & 0xFF) << 16;
        int byte0 = (getByte(resource, offset + 3) & 0xFF) << 24;
        return byte3 | byte2 | byte1 | byte0;
    }

    private static  long readLongL(byte[] resource, long offset) {
        long byte7 = (long) (getByte(resource, offset) & 0xFF);
        long byte6 = (long) (getByte(resource, offset + 1) & 0xFF) << 8;
        long byte5 = (long) (getByte(resource, offset + 2) & 0xFF) << 16;
        long byte4 = (long) (getByte(resource, offset + 3) & 0xFF) << 24;
        long byte3 = (long) (getByte(resource, offset + 4) & 0xFF) << 32;
        long byte2 = (long) (getByte(resource, offset + 5) & 0xFF) << 40;
        long byte1 = (long) (getByte(resource, offset + 6) & 0xFF) << 48;
        long byte0 = (long) (getByte(resource, offset + 7) & 0xFF) << 56;
        return byte7 | byte6 | byte5 | byte4 | byte3 | byte2 | byte1 | byte0;
    }
}
