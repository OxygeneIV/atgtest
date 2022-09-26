package atg;

import atg.pageobjects.*;
import org.junit.jupiter.api.Test;

public class AtgTests extends AtgTestBase {


    @Test
    void betOnV4() {
        // #1, Launch and handle cookies
        AtgMainPage atgMainPage = new AtgMainPage(driver);
        atgMainPage.acceptCookies();

        // #2, Select "Häst"
        atgMainPage.selectGameType(GameType.Horses);

        // #3, Open V4 (may not be shown initially, use menu)
        SideMenuPageObject sideMenu = atgMainPage.openSideMenu();

        // and return the BettingPage
        BettingPage bettingPage = sideMenu.expandAllGames().openV4();

        // Check that we can play, otherwise step 1 day forward at a time
        bettingPage.findRace();

        // #4, Make a new Coupon
        bettingPage.createNewCoupon();


        // #5a  4 horses on v4:1
        RaceCoupon raceCoupon = bettingPage.getRaceCoupon(1);
        raceCoupon.betOnRandomHorses(4);

        // 1 horse on v4:2
        raceCoupon = bettingPage.getRaceCoupon(2);
        raceCoupon.betOnRandomHorses(1);

        //2 horses on v4:3
        raceCoupon = bettingPage.getRaceCoupon(3);
        raceCoupon.betOnRandomHorses(2);

        // all horses on v4:4
        raceCoupon = bettingPage.getRaceCoupon(4);
        raceCoupon.betOnAllHorses();

        // #6, Do the betting
        bettingPage.bet();

    }

}
