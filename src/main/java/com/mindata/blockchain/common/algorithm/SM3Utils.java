package com.mindata.blockchain.common.algorithm;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author wangchenxin
 * @date 2020/7/29  11:21
 * @desc
 */
public class SM3Utils {
    /**
     * 自定义密钥加密
     * @param key
     * @param srcData
     * @return
     */
    public static byte[] hmac(byte[] key,byte[] srcData){
        KeyParameter keyParameter = new KeyParameter(key);
        SM3Digest digest = new SM3Digest();
        HMac mac = new HMac(digest);
        mac.init(keyParameter);
        mac.update(srcData,0,srcData.length);
        byte[] result = new byte[mac.getMacSize()];
        mac.doFinal(result,0);
        return result;
    }

    /**
     * 无密钥加密
     * @param srcData
     * @return
     */
    public static byte[] hash(byte[] srcData){
        SM3Digest digest = new SM3Digest();
        digest.update(srcData,0,srcData.length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result,0);
        return result;
    }

    /**
     * 固定长度=32的16进制字符串
     * @param paramStr 待加密字符串
     * @return
     */
    public static String encrypt(String paramStr){
        String resultHexString = "";
        try {
            byte[] srcData = paramStr.getBytes("UTF-8");
            byte[] resultHash = hash(srcData);
            resultHexString = ByteUtils.toHexString(resultHash);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resultHexString;
    }


    public static boolean verify(String srcStr,String sm3HexString){
        boolean flag = false;
        try {
            byte[] srcData = srcStr.getBytes("UTF-8");
            byte[] sm3Hash = ByteUtils.fromHexString(sm3HexString);
            byte[] newHash = hash(srcData);
            if(Arrays.equals(newHash,sm3Hash)){
                flag = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return flag;
    }


}
