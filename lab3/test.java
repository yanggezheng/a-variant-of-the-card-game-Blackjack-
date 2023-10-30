package lab3;

public class test {
    public static void main(String[] args){
        int M = 100;//update the M here
        int nGame = 100000;//update the number of games
        System.out.println("Reinforcement learning experiment with M = "+M+", NGames = "+nGame);
        System.out.println("\nRun 1\n");
        game g1 = new game(2,4,4,2);//it takes four parameters {1: nSides, 2: lTarget, 3: uTarget, 4: nDice}
        g1.updateM(M,nGame);//set m and nGames(the first parameter is m, and the second is nGames)
        g1.study();
        System.out.println(g1);
        System.out.println("Run 2\n");
        game g2 = new game(2,4,4,2);
        g2.updateM(M,nGame);
        g2.study();
        System.out.println(g2);
        System.out.println("Run 3\n");
        game g3 = new game(2,4,4,2);
        g3.updateM(M,nGame);
        g3.study();
        System.out.println(g3);
    }
}
