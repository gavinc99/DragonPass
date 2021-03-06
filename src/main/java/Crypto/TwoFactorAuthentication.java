package Crypto;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

public class TwoFactorAuthentication {

    //Obtaining a TOTP code using the Time based one time password algorithm
    public static String getTOTPCode(String secretKey) {

        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);

        String hexKey = Hex.encodeHexString(bytes);
        return de.taimos.totp.TOTP.getOTP(hexKey);

    }

}
