public class Outcast {
    private WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[][] distances = new int[nouns.length][nouns.length];
        for (int i = 0; i < nouns.length; ++i) {
            for (int j = i; j < nouns.length; ++j) {
                distances[i][j] = wordNet.distance(nouns[i], nouns[j]);
                distances[j][i] = distances[i][j];
            }
        }

        String noun = null;
        int maxDist = Integer.MIN_VALUE;
        for (int i = 0; i < nouns.length; ++i) {
            int distance = 0;
            for (int j = 0; j < nouns.length; ++j) {
                distance += distances[i][j];
            }
            if (distance > maxDist) {
                maxDist = distance;
                noun = nouns[i];
            }
        }
        return noun;
    }

    // for unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; ++t) {
            String[] nouns = In.readStrings(args[t]);
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}