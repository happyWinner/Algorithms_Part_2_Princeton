public class SAP {
    private Digraph G;
    private int length, ancestor;

    // constructor takes a digraph (note necessarily a DAG)
    public SAP(Digraph G) {
        this.G = new Digraph(G);
        length = -1;
        ancestor = -1;
    }

    // length of shortest ancestral path between v and w
    // -1 if no such path
    public int length(int v, int w) {
        if (!idxValid(v) || !idxValid(w)) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        sap(v,  w);
        return length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path
    // -1 if no such path
    public int ancestor(int v, int w) {
        if (!idxValid(v) || !idxValid(w)) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        sap(v, w);
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w
    // -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (!idxValid(v) || !idxValid(w)) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        sap(v, w);
        return length;
    }

    // a common ancestor that participates in shortest ancestral path
    // -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (!idxValid(v) || !idxValid(w)) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        sap(v, w);
        return ancestor;
    }

    private boolean idxValid(int idx) {
        return idx >= 0 && idx < G.V();
    }

    private boolean idxValid(Iterable<Integer> idx) {
        for (int i : idx) {
            if (i < 0 || i >= G.V()) {
                return false;
            }
        }
        return true;
    }

    private void sap(int v, int w) {
        BreadthFirstDirectedPaths vBFS = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wBFS = new BreadthFirstDirectedPaths(G, w);
        int minLength = Integer.MAX_VALUE;
        int vwAncestor = -1;
        for (int i = 0; i < G.V(); ++i) {
            if (vBFS.hasPathTo(i) && wBFS.hasPathTo(i)) {
                int vwLength = vBFS.distTo(i) + wBFS.distTo(i);
                if (minLength > vwLength) {
                    minLength = vwLength;
                    vwAncestor = i;
                }
            }
        }

        ancestor = vwAncestor;
        if (ancestor == -1) {
            length = -1;
        }
        else {
            length = minLength;
        }
    }

    private void sap(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths vBFS = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths wBFS = new BreadthFirstDirectedPaths(G, w);
        int minLength = Integer.MAX_VALUE;
        int vwAncestor = -1;
        for (int i = 0; i < G.V(); ++i) {
            if (vBFS.hasPathTo(i) && wBFS.hasPathTo(i)) {
                int vwLength = vBFS.distTo(i) + wBFS.distTo(i);
                if (minLength > vwLength) {
                    minLength = vwLength;
                    vwAncestor = i;
                }
            }
        }

        ancestor = vwAncestor;
        if (ancestor == -1) {
            length = -1;
        }
        else {
            length = minLength;
        }
    }

    // for unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}