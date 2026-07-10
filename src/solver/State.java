package solver;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class State implements Comparable<State> {

    public final int playerX;
    public final int playerY;
    public final int[] crates;
    public final String move;
    public final State parent;
    public final int g;            
    public final int h;          
    public final int f;            
    public int normalizedPlayer;

    public State(int playerX, int playerY, int[] crates, int g, int h, State parent, String move) {
        this.playerX = playerX;
        this.playerY = playerY;
        this.crates = crates;
        this.move = move;
        this.parent = parent;
        this.g = g;
        this.h = h;
        this.f = g + (4 * h); 
        this.normalizedPlayer = playerY * 1000 + playerX;
    }

    public boolean hasCrateAt(int x, int y, int width) {
        return Arrays.binarySearch(crates, y * width + x) >= 0;
    }

    @Override
    public int compareTo(State other) {
        int cmp = Integer.compare(this.f, other.f);
        if (cmp == 0) {
            return Integer.compare(other.g, this.g);
        }
        return cmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State other = (State) o;
        return this.normalizedPlayer == other.normalizedPlayer
                && Arrays.equals(this.crates, other.crates);
    }

    @Override
    public int hashCode() {
        return 31 * normalizedPlayer + Arrays.hashCode(crates);
    }

    public String reconstructPath() {
        List<String> steps = new ArrayList<>();
        State current = this;
        while (current != null && current.move != null && !current.move.isEmpty()) {
            steps.add(current.move);
            current = current.parent;
        }

        Collections.reverse(steps);
        StringBuilder sb = new StringBuilder();
        for (String s : steps) {
            sb.append(s);
        }
        return sb.toString();
    }
}