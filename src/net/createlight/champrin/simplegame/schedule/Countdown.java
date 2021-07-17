package net.createlight.champrin.simplegame.schedule;

import net.createlight.champrin.simplegame.SimpleGame;

public class Countdown {

    private String tip = SimpleGame.getInstance().config.getString("countdown-to-start-game");

    public String countDown(int number) {
        switch (number) {
            case 5:
                return getNumber5();
            case 4:
                return getNumber4();
            case 3:
                return getNumber3();
            case 2:
                return getNumber2();
            case 1:
                return getNumber1();
            default:
                return tip.replaceAll("%TIME%", String.valueOf(number));
        }
    }

    private String getNumber5() {
        return  "§c▇▇▇▇▇▇\n" +
                "§c▇           \n" +
                "§c▇▇▇▇▇▇\n" +
                "§c           ▇\n" +
                "§c▇▇▇▇▇▇\n";
    }

    private String getNumber4() {
        return  "§c▇      ▇\n" +
                "§c▇       ▇\n" +
                "§c▇▇▇▇▇\n" +
                "§c       ▇\n" +
                "§c       ▇\n";
    }

    private String getNumber3() {
        return  "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n";
    }

    private String getNumber2() {
        return  "§e▇▇▇▇▇▇\n" +
                "§e           ▇\n" +
                "§e▇▇▇▇▇▇\n" +
                "§e▇           \n" +
                "§e▇▇▇▇▇▇\n";
    }

    private String getNumber1() {
        return  "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n" +
                "§6          ▇\n";
    }
}
