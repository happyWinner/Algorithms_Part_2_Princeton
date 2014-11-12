import java.awt.Color;

public class SeamCarver {
    private static final double BORDER_ENERGY = 195075;
    private int width;
    private int height;
    private int[][] rgb;
    private double[][] energy;

    public SeamCarver(Picture picture) {
        width = picture.width();
        height = picture.height();

        rgb = new int[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                rgb[i][j] = picture.get(i, j).getRGB();
            }
        }

        energy = new double[width][height];
        calculateEnergy();
    }

    private void calculateEnergy() {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (i == 0 || i == width - 1 || j == 0 || j == height - 1) {
                    energy[i][j] = BORDER_ENERGY; 
                }
                else {
                    energy[i][j] = gradientX(i, j) + gradientY(i, j); 
                }
            }
        }
    }

    private double gradientX(int x, int y) {
        int r1 = (rgb[x+1][y] >> 16) & 0xFF;
        int r2 = (rgb[x-1][y] >> 16) & 0xFF;
        int g1 = (rgb[x+1][y] >> 8) & 0xFF;
        int g2 = (rgb[x-1][y] >> 8) & 0xFF;
        int b1 = (rgb[x+1][y] >> 0) & 0xFF;
        int b2 = (rgb[x-1][y] >> 0) & 0xFF;
        return (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2);
    }

    private double gradientY(int x, int y) {
        int r1 = (rgb[x][y+1] >> 16) & 0xFF;
        int r2 = (rgb[x][y-1] >> 16) & 0xFF;
        int g1 = (rgb[x][y+1] >> 8) & 0xFF;
        int g2 = (rgb[x][y-1] >> 8) & 0xFF;
        int b1 = (rgb[x][y+1] >> 0) & 0xFF;
        int b2 = (rgb[x][y-1] >> 0) & 0xFF;
        return (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2);
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                picture.set(i, j, new Color(rgb[i][j]));
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y in current picture
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        else {
            return energy[x][y];
        }
    }

    // sequence of indices for horizontal seam in current picture
    public int[] findHorizontalSeam() {
        int[][] edgeTo = new int[width][height];
        double[][] distTo = new double[2][height];
        for (int i = 0; i < height; ++i) {
            edgeTo[0][i] = -1; 
            distTo[0][i] = BORDER_ENERGY;
        }

        for (int i = 0; i < width - 1; ++i) {
            for (int j = 0; j < height; ++j) {
                distTo[(i+1) % 2][j] = Double.POSITIVE_INFINITY; 
            }
            for (int j = 0; j < height; ++j) {
                if (distTo[(i+1) % 2][j] > distTo[i % 2][j] + energy[i+1][j]) {
                    distTo[(i+1) % 2][j] = distTo[i % 2][j] + energy[i+1][j];
                    edgeTo[i+1][j] = j;
                }
                if (j != 0) {
                    if (distTo[(i+1) % 2][j-1] > distTo[i % 2][j] + energy[i+1][j-1]) {
                        distTo[(i+1) % 2][j-1] = distTo[i % 2][j] + energy[i+1][j-1];
                        edgeTo[i+1][j-1] = j;
                    }
                }
                if (j != height - 1) {
                    if (distTo[(i+1) % 2][j+1] > distTo[i % 2][j] + energy[i+1][j+1]) {
                        distTo[(i+1) % 2][j+1] = distTo[i % 2][j] + energy[i+1][j+1];
                        edgeTo[i+1][j+1] = j;
                    }
                }
            }
        }

        int idx = (width - 1) % 2;
        int minIdx = 0;
        double minDst = Double.POSITIVE_INFINITY; 
        for (int i = 0; i < height; ++i) {
            if (minDst > distTo[idx][i]) {
                minDst = distTo[idx][i];
                minIdx = i;
            }
        }
        int[] seamIdx = new int[width];
        for (int i = width - 1; i >= 0; --i) {
            seamIdx[i] = minIdx;
            minIdx = edgeTo[i][minIdx];
        }
        return seamIdx;
    }

    // sequence of indices for vertical seam in current picture
    public int[] findVerticalSeam() {
        int[][] edgeTo = new int[width][height];
        double[][] distTo = new double[2][width];
        for (int i = 0; i < width; ++i) {
            edgeTo[i][0] = -1; 
            distTo[0][i] = BORDER_ENERGY;
        }

        for (int i = 0; i < height - 1; ++i) {
            for (int j = 0; j < width; ++j) {
                distTo[(i+1) % 2][j] = Double.POSITIVE_INFINITY; 
            }
            for (int j = 0; j < width; ++j) {
                if (distTo[(i+1) % 2][j] > distTo[i % 2][j] + energy[j][i+1]) {
                    distTo[(i+1) % 2][j] = distTo[i % 2][j] + energy[j][i+1];
                    edgeTo[j][i+1] = j;
                }
                if (j != 0) {
                    if (distTo[(i+1) % 2][j-1] > distTo[i % 2][j] + energy[j-1][i+1]) {
                        distTo[(i+1) % 2][j-1] = distTo[i % 2][j] + energy[j-1][i+1];
                        edgeTo[j-1][i+1] = j;
                    }
                }
                if (j != width - 1) {
                    if (distTo[(i+1) % 2][j+1] > distTo[i % 2][j] + energy[j+1][i+1]) {
                        distTo[(i+1) % 2][j+1] = distTo[i % 2][j] + energy[j+1][i+1];
                        edgeTo[j+1][i+1] = j;
                    }
                }
            }
        }

        int idx = (height - 1) % 2;
        int minIdx = 0;
        double minDst = Double.POSITIVE_INFINITY; 
        for (int i = 0; i < width; ++i) {
            if (minDst > distTo[idx][i]) {
                minDst = distTo[idx][i];
                minIdx = i;
            }
        }
        int[] seamIdx = new int[height];
        for (int i = height - 1; i >= 0; --i) {
            seamIdx[i] = minIdx;
            minIdx = edgeTo[minIdx][i];
        }
        return seamIdx;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] a) {
        if (width <= 1 || height <= 1 || a.length != width) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < a.length; ++i) {
            if (a[i] < 0 || a[i] >= height) {
                throw new java.lang.IllegalArgumentException();
            }
            if (i > 0 && Math.abs(a[i] - a[i-1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }

        for (int i = 0; i < a.length; ++i) {
            for (int j = a[i] + 1; j < height; ++j) {
                rgb[i][j-1] = rgb[i][j];
            }
        }
        --height;
        calculateEnergy();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] a) {
        if (width <= 1 || height <= 1 || a.length != height) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < a.length; ++i) {
            if (a[i] < 0 || a[i] >= width) {
                throw new java.lang.IllegalArgumentException();
            }
            if (i > 0 && Math.abs(a[i] - a[i-1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }

        int[][] rgbTransposed = new int[height][width];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                rgbTransposed[j][i] = rgb[i][j]; 
            }
        }
        for (int i = 0; i < a.length; ++i) {
            for (int j = a[i] + 1; j < width; ++j) {
                rgbTransposed[i][j-1] = rgbTransposed[i][j];
            }
        }
        --width;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                rgb[i][j] = rgbTransposed[j][i];
            }
        }
        calculateEnergy();
    }
}