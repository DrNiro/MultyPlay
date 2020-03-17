package com.example.multyplay;

import java.util.ArrayList;

public class GamesConstants {

    public static final String CALL_OF_DUTY_3_NAME = "Call of Duty 3";
    public static final String FIFA_2020_NAME = "FIFA20";
    public static final String THE_WITCHER_1_NAME = "The Witcher";
    public static final String THE_WITCHER_2_NAME = "The Witcher 2";
    public static final String THE_WITCHER_3_NAME = "The Witcher 3: Wild Hunt";
    public static final String GOD_OF_WAR_1_NAME = "God of War";
    public static final String BATTLEFIELD_4_NAME = "Battlefield 4";
    public static final String DIABLO_1_NAME = "Diablo";
    public static final String DIABLO_2_NAME = "Diablo II";
    public static final String DIABLO_2_LOD_NAME = "Diablo II LOD";
    public static final String DIABLO_3_NAME = "Diablo III";
    public static final String DIABLO_3_ROS_NAME = "Diablo III ROS";
    public static final String DIABLO_4_NAME = "Diablo IV";
    public static final String GTA_5_NAME = "GTA V";


    public static final Game CALL_OF_DUTY_3  = new Game(CALL_OF_DUTY_3_NAME, "Shooting", true, R.drawable.img_cod_3);
    public static final Game FIFA_2020 = new Game(FIFA_2020_NAME, "sports", true, R.drawable.img_fifa_20);
//    public static final Game THE_WITCHER_1 = new Game();
//    public static final Game THE_WITCHER_2 = new Game();
    public static final Game THE_WITCHER_3 = new Game(THE_WITCHER_3_NAME, "Awesome", false, R.drawable.img_witcher_iii);
//    public static final Game GOD_OF_WAR_1 = new Game();
//    public static final Game BATTLEFIELD_4 = new Game();
//    public static final Game DIABLO_1 = new Game();
//    public static final Game DIABLO_2 = new Game();
//    public static final Game DIABLO_2_LOD = new Game();
    public static final Game DIABLO_3 = new Game(DIABLO_3_NAME, "Awesome", true, R.drawable.img_diablo_iii);
//    public static final Game DIABLO_3_ROS = new Game();
//    public static final Game DIABLO_4 = new Game();
//    public static final Game GTA_5 = new Game();



    public static ArrayList<Game> getAllLangsList() {
        ArrayList<Game> allGamesList = new ArrayList<>();
        allGamesList.add(CALL_OF_DUTY_3);
        allGamesList.add(FIFA_2020);
        allGamesList.add(THE_WITCHER_3);
        allGamesList.add(DIABLO_3);

        return allGamesList;
    }


}
