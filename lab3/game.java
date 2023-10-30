package lab3;

import java.util.ArrayList;

public class game {
    private final int nSides;
    private final int lTarget;
    private final int uTarget;
    private final int nDice;
    private static int[][][] winCount;
    private static int[][][] loseCount;
    private static double[][][] fn;
    private static double[][][] pn;
    private static int[][] bn;
    private int m;
    private int nGames;

    int getNumber(int nSides) {
        return (int) (Math.random() * nSides + 1);
    }//the number on the dice

    void playGame() {
        int myTotal = 0, yourTotal = 0, myChoice, yourChoice;
        ArrayList<Integer> myList = new ArrayList<>();
        ArrayList<Integer> yourList = new ArrayList<>();//store each scenario and choices
        while (myTotal < lTarget && yourTotal < lTarget) {
            myChoice = findBest(myTotal, yourTotal);
            myList.add(myTotal);
            myList.add(yourTotal);
            myList.add(myChoice);
            for (int i = 0; i < myChoice + 1; i++) myTotal += getNumber(nSides);//roll dice
            if (myTotal >= lTarget) break;
            yourChoice = findBest(yourTotal, myTotal);
            yourList.add(yourTotal);
            yourList.add(myTotal);
            yourList.add(yourChoice);
            for (int i = 0; i < yourChoice + 1; i++) yourTotal += getNumber(nSides);
        }
        if ((yourTotal <= uTarget && yourTotal >= lTarget) || myTotal > uTarget) {//update data
            for (int i = 0; i < myList.size(); i += 3) loseCount[myList.get(i)][myList.get(i + 1)][myList.get(i + 2)]++;
            for (int i = 0; i < yourList.size(); i += 3)
                winCount[yourList.get(i)][yourList.get(i + 1)][yourList.get(i + 2)]++;
        } else {
            for (int i = 0; i < myList.size(); i += 3) winCount[myList.get(i)][myList.get(i + 1)][myList.get(i + 2)]++;
            for (int i = 0; i < yourList.size(); i += 3)
                loseCount[yourList.get(i)][yourList.get(i + 1)][yourList.get(i + 2)]++;
        }
    }

    void study() {
        for (int i = 0; i < nGames; i++) playGame();
    }//learning process

    void update(int lTarget, int nDice) {//create arrays to store the data
        winCount = new int[lTarget][lTarget][nDice];
        loseCount = new int[lTarget][lTarget][nDice];
        fn = new double[lTarget][lTarget][nDice];
        pn = new double[lTarget][lTarget][nDice];
        bn = new int[lTarget][lTarget];
    }

    void updateM(int m, int nGames) {//update m and nGames
        this.m = m;
        this.nGames = nGames;
    }

    void findFn() {//update the value of f according to the formula
        for (int j = 0; j < lTarget; j++) {
            for (int k = 0; k < lTarget; k++) {
                for (int i = 0; i < nDice; i++) {
                    if (winCount[j][k][i] == 0 && loseCount[j][k][i] == 0)
                        fn[j][k][i] = (double) 1 / nDice;
                    else {
                        fn[j][k][i] = (double) winCount[j][k][i] / (winCount[j][k][i] + loseCount[j][k][i]);
                    }
                }
            }
        }
    }

    int findBest(int myTotal, int yourTotal) {//find your choice using random cumulative distribution given my score and your score.
        findFn();
        findB();
        findPn();
        double rand = Math.random();
        int i = 0;
        while (rand > pn[myTotal][yourTotal][i]) {
            rand -= pn[myTotal][yourTotal][i];
            if (i < nDice - 1) i++;
            else break;
        }
        return i;
    }

    void findPn() {//find the prob according to the formula provided
        for (int j = 0; j < lTarget; j++) {
            for (int k = 0; k < lTarget; k++) {
                int index = bn[j][k];
                int t = findT(j, k);
                double bestP = (t * fn[j][k][index] + m) / (t * fn[j][k][index] + nDice * m);
                for (int i = 0; i < nDice; i++) {
                    if (i == index) pn[j][k][i] = bestP;
                    else pn[j][k][i] = (1 - bestP) * (t * fn[j][k][i] + m) / (t * findG(j, k) + (nDice - 1) * m);
                }
            }
        }
    }

    void findB() {//find the best number of dices to roll (the output + 1 should give you the best EX: 0 means the best choice is to roll 1 dice)
        for (int j = 0; j < lTarget; j++) {
            for (int k = 0; k < lTarget; k++) {
                int index = 0;
                for (int i = 1; i < nDice; i++) if (fn[j][k][i] > fn[j][k][index]) index = i;
                bn[j][k] = index;
            }
        }
    }

    double findG(int myTotal, int yourTotal) {//find the G at given my score and your score
        double count = 0;
        int index = bn[myTotal][yourTotal];
        for (int i = 0; i < nDice; i++) if (i != index) count += fn[myTotal][yourTotal][i];
        return count;
    }

    int findT(int myTotal, int yourTotal) {//find the T at given my score and your score
        int count = 0;
        for (int i = 0; i < nDice; i++) {
            count += winCount[myTotal][yourTotal][i];
            count += loseCount[myTotal][yourTotal][i];
        }
        return count;
    }

    game(int nSides, int lTarget, int uTarget, int nDice) {//initialize the game
        this.nSides = nSides;
        this.lTarget = lTarget;
        this.uTarget = uTarget;
        this.nDice = nDice;
        update(lTarget, nDice);
    }

    public String toString() {//toString method that output the result
        findFn();
        findB();
        findPn();
        StringBuilder sb = new StringBuilder();
        sb.append("Play\n\n\n");
        for (int i = 0; i < lTarget; i++) {//play block
            for (int j = 0; j < lTarget; j++) {
                if (winCount[i][j][bn[i][j]] == 0 && loseCount[i][j][bn[i][j]] == 0) sb.append(0).append("\t");
                else sb.append(bn[i][j] + 1).append("\t");
            }
            sb.append("\n");
        }
        sb.append("\nProb\n\n\n");
        for (int i = 0; i < lTarget; i++) {//prob block
            for (int j = 0; j < lTarget; j++) {
                if (winCount[i][j][bn[i][j]] == 0 && loseCount[i][j][bn[i][j]] == 0) sb.append(0).append("\t\t\t");
                else
                    sb.append(String.format("%.4f", (double) winCount[i][j][bn[i][j]] / (winCount[i][j][bn[i][j]] + loseCount[i][j][bn[i][j]]))).append("\t\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
