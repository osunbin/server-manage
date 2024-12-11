package com.bin.sm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

public class CRC32Util {
   private static final int BUFFER_SIZE = 512;

   /**
    * 编码
    * 
    * @param data
    * @return
    */
   public static long encode(byte[] data) {
      CRC32 crc32 = new CRC32();
      crc32.update(data);
      return crc32.getValue();
   }

   /**
    * 编码
    * 
    * @param data
    * @return
    */
   public static long encode(InputStream data) {
      try {
         byte[] buffer = new byte[BUFFER_SIZE];
         int read = data.read(buffer, 0, BUFFER_SIZE);
         CRC32 crc32 = new CRC32();
         while (read > -1) {
            crc32.update(buffer, 0, read);
            read = data.read(buffer, 0, BUFFER_SIZE);
         }
         return crc32.getValue();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}