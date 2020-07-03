package com.company;


import java.util.Scanner;

public class Main {
    public static void printDivider() {
        System.out.println("\n--------------------------------------------------------------------------------------------\n");
    }
    public static class Chances {
        private final float f0 = 0.1f;
        private final float s0 = 0.03f;
        private final float t0 = 0.02f;

        private final int x0 = 23;
        public int x = x0;

        public float f = f0;
        public float s = s0;
        public float t = t0;

        private float eventChance() {
            return 1.0f - f - s - t;
        }

        private String fToS(float f) {
            return String.format("%.02f%%", f * 100.0f);
        }

        public void print() {
            float e = eventChance();

            System.out.println("Current Params:");
            System.out.println();
            System.out.println("Fight chance: " + fToS(f));
            System.out.println("Treasure chance: " + fToS(t));
            System.out.println("Shop chance: " + fToS(s));
            System.out.println("Event chance: " + fToS(e) + " (" + fToS(0.75f * e) + " actual events, " + fToS(0.25f * e) + " shrines)");
            System.out.println("Number of events remaining in pool: " + x);
            System.out.println();

            chanceOfSeeingEventAfter2();
            printDivider();
        }

        public void resetAll() {
            f = f0;
            s = s0;
            t = t0;
            x = x0;
        }

        private void rampAll() {
            f += f0;
            s += s0;
            t += t0;
        }

        public void seeFight() {
            System.out.println("FIGHT\n");
            rampAll();
            f = f0;
        }

        public void seeShop() {
            System.out.println("SHOP\n");
            rampAll();
            s = s0;
        }

        public void seeEvent() {
            System.out.println("EVENT\n");
            rampAll();
            x -= 1;
        }

        public void seeShrine() {
            System.out.println("SHRINE\n");
            rampAll();
        }

        public void seeTreasure() {
            System.out.println("TREASURE\n");
            rampAll();
            t = t0;
        }

        public float chanceOfSeeingEventAfter2() {
            // Not enough events in the pool
            if (x < 1)
                return 0.0f;

            // Cast int x to float for remainder
            final float fx = (float)x;
            final float pc = 1.0f / fx; // chance to pull specific event in the event pool of size x

            // Ramp up constants (TODO: pull from AbstractDungeon?)
            final float fr = 0.1f;  // fight ramp
            final float tr = 0.02f; // treasure ramp
            final float sr = 0.03f; // shop ramp

            // Ramp resets (TODO: pull from AbstractDungeon?)
            // note: by default, these reset values are the same as the ramps
            final float f0 = fr;
            final float t0 = tr;
            final float s0 = sr;

            // Chance to see any event/shrine
            final float E = 1.0f - t - f - s;

            // Roll event: 75% of the time it is a regular event; 25% of the time it is a shrine
            // Regular events get pulled from pool of size x at the start, shrines from a shrine pool (who cares)
            final float evtShrineRatio = 0.75f;
            final float shrineEvtRatio = 0.25f;

        /*----------------------+
        |  First question mark  |
        +----------------------*/

            // A1 = chance we got our desired event on the first ? floor
            final float A1 = E * evtShrineRatio * (1.0f / fx);

            // A2 = chance we saw a different event on the first ? floor
            final float A2 = E * evtShrineRatio * ((fx - 1.0f) / fx);

            // A3 = chance we saw a shrine on the first ? floor
            final float A3 = E * shrineEvtRatio;

            // A4 = chance we saw a fight on the first ? floor
            final float A4 = f;

            // A5 = chance we saw a treasure on the first ? floor
            final float A5 = t;

            // A6 = chance we saw a shop on the first ? floor
            final float A6 = s;

        /*-----------------------+
        |  Second question mark  |
        +-----------------------*/

            // Ramp-ups
            // all ramp up, fight reset others ramp up, treasure reset others ramp up, etc.
            final float fullRamp = 1.0f - f - fr - t - tr - s - sr;
            final float fightRamp = 1.0f - f0 - t - tr - s - sr;
            final float treasureRamp = 1.0f - f - fr - t0 - s - sr;
            final float shopRamp = 1.0f - f - fr - t - tr - s0;

            // B1A1 = chance we've seen our desired event after 2 floors given that we've seen it on the first
            final float B1A1 = 1.0f; // duh

            // B1A2 = chance we see our event on the second ? floor after seeing a diff. event on the first
            final float B1A2 = (x > 1) ? fullRamp * evtShrineRatio * (1.0f / (fx - 1.0f)) : 1.0f;

            // B1A3 = chance we see our evt on second ? after seeing a shrine on first
            final float B1A3 = fullRamp * evtShrineRatio * pc;

            // B1A4 = chance we see evt on second ? after seeing a fight on first
            final float B1A4 = fightRamp * evtShrineRatio * pc;

            // B1A5 = chance we see evt on second ? after seeing a treasure on first
            final float B1A5 = treasureRamp * evtShrineRatio * pc;

            // B1A6 = chance we see evt on second ? after seeing a shop on first
            final float B1A6 = shopRamp * evtShrineRatio * pc;

            // Final chance to see the desired event after at least two floors:
            final float prAfter2 = A1 + (B1A2 * A2) + (B1A3 * A3) + (B1A4 * A4) + (B1A5 * A5) + (B1A6 * A6);

            System.out.println("******************************");
            System.out.println("|  After 1 ? floor:   " + fToS(A1) + "  |");
            System.out.println("|  After 2 ? floor:   " + fToS(prAfter2) + "  |");
            System.out.println("******************************");
            return prAfter2;
        }
    }
    public static void printCommands() {
        System.out.println("Enter a command (case-insensitive):");
        System.out.println("[Q]uit the application");
        System.out.println("[R]eset to initial values");
        System.out.println("[E]dit the number of events in the pool manually");

        System.out.println("\nThe following commands force an encountered ? and updates the params:");
        System.out.println("[F] - Fight");
        System.out.println("[S] - Shop");
        System.out.println("[T] - Treasure");
        System.out.println("[O] - Other Event");
        System.out.println("[X] - Shrine");

        printDivider();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        Chances c = new Chances();

        boolean shouldLoop = true;

        while (shouldLoop) {
            c.print();

            printCommands();

            String command = in.nextLine();
            command = command.toLowerCase();
            //System.out.println("You entered '" + command + "'");

            switch (command) {
                case "q":
                    System.out.println("Quitting!");
                    shouldLoop = false;
                    break;
                case "r":
                    System.out.println("Reset!");
                    break;
                case "e":
                    System.out.println("You selected (edit). Input an integer (default 23) for the number of events in the pool: ");
                    int new_x = in.nextInt();
                    c.x = new_x;

                    break;
                case "f":
                    System.out.println("Fight");
                    c.seeFight();
                    break;
                case "s":
                    System.out.println("Shop");
                    c.seeShop();
                    break;
                case "t":
                    System.out.println("Treasure");
                    c.seeTreasure();
                    break;
                case "o":
                    System.out.println("Other Event");
                    c.seeEvent();
                    break;
                case "x":
                    System.out.println("Shrine");
                    c.seeShrine();
                    break;
                default:
                    System.out.println("Unrecognized, try again");
            }
        }

        //c.seeEvent();
        //c.seeFight();
        //c.seeShop();
        //c.seeShrine();

        //c.print();
    }
}
