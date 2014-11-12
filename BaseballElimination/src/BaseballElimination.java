public class BaseballElimination {
    private ST<String, Integer> teams;
    private ST<Integer, String> invTeams;
    private int[] w;
    private int[] l;
    private int[] r;
    private int[][] g;
    private int numVertices;
    private String prevQuery;
    private boolean prevEliminated;
    private Queue<String> prevR;

    // create a baseball division from given filename
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int N = in.readInt();
        teams = new ST<String, Integer>();
        invTeams = new ST<Integer, String>();
        w = new int[N];
        l = new int[N];
        r = new int[N];
        g = new int[N][N];
        prevQuery = null;
        numVertices = (N - 1) * (N - 2) / 2 + N + 1;

        for (int i = 0; i < N; ++i) {
            String team = in.readString();
            teams.put(team, Integer.valueOf(i));
            invTeams.put(Integer.valueOf(i), team);
            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();
            for (int j = 0; j < N; ++j) {
                g[i][j] = in.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teams;
    }

    // number of wins for given team
    public int wins(String team) {
        if (argCheck(team)) {
            return w[teams.get(team).intValue()];
        }
        else {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // number of losses for given team
    public int losses(String team) {
        if (argCheck(team)) {
            return l[teams.get(team).intValue()];
        }
        else {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (argCheck(team)) {
            return r[teams.get(team).intValue()];
        }
        else {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (argCheck(team1) && argCheck(team2)) {
            return g[teams.get(team1).intValue()][teams.get(team2).intValue()];
        }
        else {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!argCheck(team)) {
            throw new java.lang.IllegalArgumentException();
        }

        if (prevQuery != null && team.compareTo(prevQuery) == 0) {
            return prevEliminated;
        }
        else {
            int teamIdx = teams.get(team);
            prevQuery = team;
            prevR = new Queue<String>();

            // trivial elimination
            for (int i = 0; i < teams.size(); ++i) {
                if (w[teamIdx] + r[teamIdx] < w[i]) {
                    prevR.enqueue(invTeams.get(Integer.valueOf(i)));
                    prevEliminated = true;
                    return true;
                }
            }

            // nontrivial elimination
            FlowNetwork graph = new FlowNetwork(numVertices);
            int k = 1;
            for (int i = 0; i < teams.size(); ++i) {
                for (int j = i + 1; j < teams.size(); ++j) {
                    if (i != teamIdx && j != teamIdx) {
                        graph.addEdge(new FlowEdge(0, k++, g[i][j]));
                    }
                }
            }
            k = 1;
            for (int i = numVertices - teams.size(); i < numVertices - 1; ++i) {
                for (int j = i + 1; j < numVertices - 1; ++j) {
                    if (i != teamIdx && j != teamIdx) {
                        graph.addEdge(new FlowEdge(k, i, Double.POSITIVE_INFINITY));
                        graph.addEdge(new FlowEdge(k++, j, Double.POSITIVE_INFINITY));
                    }
                }
            }
            k = numVertices - teams.size();
            for (int i = 0; i < teams.size(); ++i) {
                if (i != teamIdx) {
                    graph.addEdge(new FlowEdge(k++, numVertices - 1, w[teamIdx] + r[teamIdx] - w[i]));
                }
            }
            //StdOut.println(graph.toString());
            FordFulkerson ff = new FordFulkerson(graph, 0, numVertices - 1);
            SET<Integer> set = new SET<Integer>();
            for (int i = 1; i <= numVertices - teams.size() - 1; ++i) {
                if (ff.inCut(i)) {
                    for (FlowEdge edge : graph.adj(i)) {
                        int idx = edge.to() - numVertices + teams.size();
                        if (idx >= 0) {
                            if (idx >= teamIdx) {
                                ++idx;
                            }
                            if (!set.contains(Integer.valueOf(idx))) {
                                set.add(Integer.valueOf(idx));
                                prevR.enqueue(invTeams.get(Integer.valueOf(idx)));
                            }
                        }
                    }
                }
            }
            if (prevR.size() == 0) {
                prevEliminated = false;
            }
            else {
                prevEliminated = true;
            }
            return prevEliminated;
        }
    }

    // subset R of teams that eliminates given team
    // null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!argCheck(team)) {
            throw new java.lang.IllegalArgumentException();
        }

        if (isEliminated(team)) {
            return prevR;
        }
        else {
            return null;
        }
    }

    private boolean argCheck(String team) {
        return teams.contains(team);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}