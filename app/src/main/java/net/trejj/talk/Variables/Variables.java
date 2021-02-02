package net.trejj.talk.Variables;
/** Created by AwsmCreators * */
public class Variables {
    //Setup google play billing details
    //public static String licence_key ="playstore public key";
    //public static String MERCHANT_ID = "your playstore merchant key";

    //Setup google pay(tez) upi payment id for indian users only
    //public static String UPI_ID ="yourupi@okaxis";
    //public static String UPI_NAME = "Your upi name";

    //enable or disable UPI   //True to Enable, False to Disable
    //public static Boolean ENABLE_UPI = true;

    //set your own welcome credits // Do not put number alone/ for example if u want to give 100 credits, make it 100.0
    //or if you want to set 0 , make it 0.0 ==== otherwise app will crash
    public static Double WELCOME_CREDITS = 10.0;

    //minimum credits need to make a call
    public static Double MINIMUM_CREDITS = 200.0;

    //Minimum call rate if somehow system undetected the country code
    public static Double MINIMUM_CALL_RATE = 1200.0;

    //Setup credit packs
    public static Integer SMALL_PACK_CREDITS = 5000;
    public static Integer MEDIUM_PACK_CREDITS = 20000;
    public static Integer BIG_PACK_CREDITS = 60000;

    //setup pack names
    //public static String SMALL_PACK_NAME = "Mini pack";
   // public static String MEDIUM_PACK_NAME = "Smart pack";
    //public static String BIG_PACK_NAME = "Big bundle";

    //SETUP PACKAGE COST FOR (UPI) INDIAN USERS (INR) Indian rupee
    //public static Integer SMALL_PACK_COST = 1;//INR
    //public static Integer MEDIUM_PACK_COST = 100;//INR
    //public static Integer BIG_PACK_COST = 300;//INR
}
