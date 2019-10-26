package sample;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ProvaD {

    public static void main(String[] args) {

        BigDecimal d = new BigDecimal(23).setScale(2, RoundingMode.HALF_DOWN);
        System.out.println(d);
        String num = " 133.45 ";
        num  = num.replaceAll("\\s+","");
        System.out.println(num);
        BigDecimal number = new BigDecimal(num );
        //NumberFormat nf = NumberFormat.getCurrencyInstance();
        //String pattern = "###,###.##";
        //DecimalFormat df = (DecimalFormat)nf;
        //DecimalFormat df = new DecimalFormat(pattern);
        //System.out.println(df.format(number));
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.ITALY);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern("###,##0.00");
        System.out.println(df.format(number));

    }
}
