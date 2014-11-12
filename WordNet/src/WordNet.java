public class WordNet {
    private SeparateChainingHashST<String, Bag<Integer>> hash;
    private SeparateChainingHashST<Integer, String> synset;
    private Digraph G;
    private Queue<String> nouns; 
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        hash = new SeparateChainingHashST<String, Bag<Integer>>();
        synset = new SeparateChainingHashST<Integer, String>();
        nouns = new Queue<String>(); 
        In in = new In(synsets);
        int numID = 0;
        for (String line = in.readLine(); line != null; line = in.readLine()) {
           String[] fields = line.split("\\,");
           synset.put(Integer.valueOf(fields[0]), fields[1]);
           String[] words = fields[1].split(" ");
           for (int i = 0; i < words.length; ++i) {
               if (hash.contains(words[i])) {
                   hash.get(words[i]).add(Integer.valueOf(fields[0]));
               }
               else {
                   Bag<Integer> bag = new Bag<Integer>();
                   bag.add(Integer.valueOf(fields[0]));
                   hash.put(words[i], bag);
                   nouns.enqueue(words[i]);
               }
           }
           ++numID;
        }

        G = new Digraph(numID);
        in = new In(hypernyms);
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            String[] fields = line.split("\\,");
            for (int i = 1; i < fields.length; ++i) {
                G.addEdge(Integer.valueOf(fields[0]), Integer.valueOf(fields[i]));
            }
        }

        int numRoot = 0;
        // check whether the input is a rooted DAG
        for (int i = 0; i < G.V(); ++i) {
            if (((Bag<Integer>) (G.adj(i))).isEmpty()) {
                ++numRoot;
                if (numRoot > 1) {
                    throw new java.lang.IllegalArgumentException();
                }
            }
        }
        DirectedCycle cycle = new DirectedCycle(G);
        if (cycle.hasCycle()) {
            throw new java.lang.IllegalArgumentException();
        }

        sap = new SAP(G);
    }

    // the set of nouns (no duplicates), returned as an Iterable
    public Iterable<String> nouns() {
        return nouns;        
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return hash.contains(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }
        return sap.length(hash.get(nounA), hash.get(nounB));
    }

    // a synset that is the common ancestor of nounA and nounB in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }

        int ancestorID = sap.ancestor(hash.get(nounA), hash.get(nounB));
        if (ancestorID == -1) {
            return null;
        }
        else {
            return synset.get(ancestorID);
        }
    }
}