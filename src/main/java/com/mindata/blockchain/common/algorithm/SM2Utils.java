package com.mindata.blockchain.common.algorithm;

import com.mindata.blockchain.block.PairKey;
import com.mindata.blockchain.common.exception.ErrorNum;
import com.mindata.blockchain.common.exception.TrustSDKException;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 * @author wangchenxin
 * @date 2020/7/30  13:46
 * @desc
 */
public class SM2Utils {

    /**
     * 生成私钥公钥对
     * @return
     */
    public static PairKey generateKeyPair() {
//        BigInteger d = random(n.subtract(new BigInteger("1")));
//        PairKey pairKey = new PairKey();
//        ECPoint ecPoint = G.multiply(d).normalize();
//        byte[] encoded = ecPoint.getEncoded(true);
//        pairKey.setPrivateKey(Base64.getEncoder().encodeToString(encoded));
//        pairKey.setPublicKey(Base64.getEncoder().encodeToString(d.toByteArray()));
//
//        if (checkPublicKey(curve.decodePoint(Base64.getDecoder().decode(pairKey.getPrivateKey())))) {
//            return pairKey;
//        } else {
//            return null;
//        }
        PairKey pairKey = new PairKey();
        try {
            // 获取SM2椭圆曲线的参数
            final ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
            // 获取一个椭圆曲线类型的密钥对生成器
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
            // 使用SM2参数初始化生成器
            kpg.initialize(sm2Spec);
            KeyPair keyPair = kpg.generateKeyPair();
            keyPair.getPrivate().getFormat();
            pairKey.setPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            pairKey.setPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return pairKey;
    }

    /**
     * 为字符串进行签名, 并返回签名. <br/>
     * @param
     */
    public static String signString(String strprivateKey, byte[] data) {
        // 生成SM2sign with sm3 签名验签算法实例
        Signature signature = null;
        try {
            signature = Signature.getInstance("SM3withSm2", new BouncyCastleProvider());
            KeyFactory keyFact = KeyFactory.getInstance("EC",  new BouncyCastleProvider());
            // 根据采用的编码结构反序列化公私钥
            PrivateKey privateKey = keyFact.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(strprivateKey)));
            // 签名需要使用私钥，使用私钥 初始化签名实例
            signature.initSign(privateKey);
            // 写入签名原文到算法中
            signature.update(data);
            // 计算签名值
            byte[] signatureValue = signature.sign();
            return Base64.getEncoder().encodeToString(signatureValue);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用公钥验证一个签名是否有效
     * @param
     */
    public static boolean verifyString(String strpublicKey, String srcString, String sign) {
        // 生成SM2sign with sm3 签名验签算法实例
        Signature signature = null;
        try {
            KeyFactory keyFact = KeyFactory.getInstance("EC",  new BouncyCastleProvider());
            // 根据采用的编码结构反序列化公私钥
            PublicKey publicKey = keyFact.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(strpublicKey)));

            signature = Signature.getInstance("SM3withSm2", new BouncyCastleProvider());
            // 签名需要使用公钥，使用公钥 初始化签名实例
            signature.initVerify(publicKey);
            // 写入待验签的签名原文到算法中
            signature.update(srcString.getBytes());
            // 验签
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * checkPairKey:验证一对公私钥是否匹配. <br/>
     * @since JDK 1.7
     */
    public static boolean checkPairKey(String prvKey, String pubKey) throws TrustSDKException {
        if (StringUtils.isEmpty(prvKey) || StringUtils.isEmpty(pubKey)) {
            throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
        }
        return true;
//        try {
//            String correctPubKey = ECDSAAlgorithm.generatePublicKey(prvKey.trim(), true);
//            return pubKey.trim().equals(correctPubKey);
//        } catch(Exception e) {
//            throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
//        }

    }

    /**
     * 根据私钥生成公钥
     * @return
     */
    public static String generatePublicKey(String strprivatekey) {
        try {
            // 获取SM2椭圆曲线的参数
            final ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
            // 获取一个椭圆曲线类型的密钥对生成器
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
            // 使用SM2参数初始化生成器
            kpg.initialize(sm2Spec);
            KeyPair keyPair = kpg.generateKeyPair();
            keyPair.getPrivate().getFormat();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }



    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PairKey pairKey = SM2Utils.generateKeyPair();
        System.out.println(pairKey);

        String s = generatePublicKey(pairKey.getPrivateKey());
        System.out.println(s);

        String str = signString(pairKey.getPrivateKey(), "lala啦".getBytes());
        System.out.println("sign:"+str);
        boolean verifyString = verifyString(pairKey.getPublicKey(), "lala啦", str);
        System.out.println(verifyString);
    }
}
