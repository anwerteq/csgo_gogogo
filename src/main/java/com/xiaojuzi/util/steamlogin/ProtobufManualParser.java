package com.xiaojuzi.util.steamlogin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ProtobufManualParser {

    public static void main(String[] args) throws IOException {
        String basestr= "CIy9g8zRkpS9WBIQOJuFUs3YXYAm5R1KH7uEmR0AAKBAIgIIAyj5/56XlYCAiAEy1wNleUFpZEhsd0lqb2dJa3BYVkNJc0lDSmhiR2NpT2lBaVJXUkVVMEVpSUgwLmV5QWlhWE56SWpvZ0ltTTZOak0zTlRRNU5qZ3lNemd3TkRVNE16VTJOQ0lzSUNKemRXSWlPaUFpTnpZMU5qRXhPVGt6TlRFeE9EVTBNREVpTENBaVlYVmtJam9nV3lBaWQy";
        byte[] decode = Base64.getDecoder().decode(basestr);
        
        // 解析二进制数据
        parseProtobufMessage(decode);
    }

    public static void parseProtobufMessage(byte[] data) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(data);
        
        while (inputStream.available() > 0) {
            // Step 1: 读取字段的 key (field_number << 3 | wire_type)
            int key = (int) readVarint(inputStream);
            int fieldNumber = key >> 3;
            int wireType = key & 0x07;

            System.out.println("Field Number: " + fieldNumber);
            System.out.println("Wire Type: " + wireType);

            // Step 2: 根据 wire_type 读取相应的值
            switch (wireType) {
                case 0: // Varint 类型
                    long varintValue = readVarint(inputStream);
                    System.out.println("Varint Value: " + varintValue);
                    break;
                case 1: // 64-bit 固定长度
                    long fixed64Value = readFixed64(inputStream);
                    System.out.println("Fixed64 Value: " + fixed64Value);
                    break;
                case 2: // Length-delimited 类型
                    int length = (int) readVarint(inputStream); // 先读取长度
                    byte[] bytes = new byte[length];
                    inputStream.read(bytes);
                    String stringValue = new String(bytes);
                    System.out.println("String/Bytes Value: " + stringValue);
                    break;
                case 5: // 32-bit 固定长度
                    int fixed32Value = readFixed32(inputStream);
                    System.out.println("Fixed32 Value: " + fixed32Value);
                    break;
                default:
                    throw new IOException("Unknown wire type: " + wireType);
            }
            System.out.println();
        }
    }

    // 读取 Varint 值 (Protobuf 可变长编码的整数)
    public static long readVarint(InputStream inputStream) throws IOException {
        long result = 0;
        int shift = 0;
        while (true) {
            byte b = (byte) inputStream.read();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                break;
            }
            shift += 7;
        }
        return result;
    }

    // 读取 32-bit 固定长度值
    public static int readFixed32(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4];
        inputStream.read(buffer);
        return ((buffer[0] & 0xFF)) |
               ((buffer[1] & 0xFF) << 8) |
               ((buffer[2] & 0xFF) << 16) |
               ((buffer[3] & 0xFF) << 24);
    }

    // 读取 64-bit 固定长度值
    public static long readFixed64(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8];
        inputStream.read(buffer);
        return ((buffer[0] & 0xFFL)) |
               ((buffer[1] & 0xFFL) << 8) |
               ((buffer[2] & 0xFFL) << 16) |
               ((buffer[3] & 0xFFL) << 24) |
               ((buffer[4] & 0xFFL) << 32) |
               ((buffer[5] & 0xFFL) << 40) |
               ((buffer[6] & 0xFFL) << 48) |
               ((buffer[7] & 0xFFL) << 56);
    }
}
