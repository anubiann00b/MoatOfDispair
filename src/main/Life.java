package main;

public class Life {

    private int x, y;
    private int life = 10;
    private boolean alive = false;
    private boolean infected = false;
    private int rate = 0;
    private Infection virus;
    private int illness = 30;
    private boolean spread = true;

    public int time = 100;
    
    public boolean isAlive() { return alive; }
    public boolean isInfected() { return infected; }
    
    public void setAlive(boolean alive) { this.alive = alive; }
    
    public Life(int x, int y, boolean alive) {
        this.x = x;
        this.y = y;
        this.alive = alive;
    }

    public boolean update(Life[][] grid) {
        int near = 0;
        try {
            near = getAdjacent(grid);
        } catch (NullPointerException e) { }
        
        if (near == 3 && illness > 0) {
            alive = true;
            if (life == -time)
                life = 10;
        }
        
        if (near >= 4 || near < 2)
            alive = false;
        
        if (alive)
            if (life == 0) {
                alive = false;
                life--;
            }
        else if (life != -time)
            life--;
        
        if (life % 10 == 0) {
            if (x > 15 && y > 15 && x < Main.winWidth-15 && y < Main.winHeight-15 && x % 60 == 0 && y % 60 == 0) {
                int density = density(grid);
                if (density > 250) {
                    int factor = (100 - (200 - density)) / 5;
                    if (factor < 1)
                        factor = 1;

                    if ((int)(Math.random()*factor) == 0) {
                        infected = true;
                        rate = factor;
                        virus = new Infection();
                    }
                }
            }
        }
        
        if (infected)
            infectUpdate(grid);
        
        if (illness == -30) {
            illness = 30;
            infected = false;
            virus = null;
            rate = 0;
            spread = true;
        }
        return alive;
    }
    
    // This method is broken? Life always spreads downward.
    public int getAdjacent(Life[][] grid) throws NullPointerException {
        int count = 0;
        if (x>1) {
            if (grid[x-1][y].alive)
                count++;
            if (y>1 && grid[x-1][y-1].alive)
                count++;
            if (y<Main.winHeight-1 && grid[x-1][y+1].alive)
                count++;
        }
        if (y>1 && grid[x][y-1].alive)
            count++;

        if (x<Main.winWidth-1) {
            if (grid[x+1][y].alive)
                count++;
            if (y<Main.winHeight-1 && grid[x+1][y+1].alive)
                count++;
            if (y>1 && grid[x+1][y-1].alive)
                count++;
        }
        if (y<Main.winHeight-1 && grid[x][y+1].alive)
            count++;

        return count;
    }

    public int density(Life[][] grid) {
        int count = 0;
        for (int ix=this.x-15;ix<this.x+15;ix++)
            for (int iy=this.y-15;iy<this.y+15;iy++)
                try {
                    if (grid[ix][iy].alive)
                        count++;
                } catch(NullPointerException e) { }
        return count;
    }

    public void infectUpdate(Life[][] grid) {
        if (spread && (int)(Math.random()*((rate / 2) + 1)) == 0) {
            if (x>1 && grid[x-1][y].alive)
                grid[x-1][y].infect(virus);
            if (x<999 && grid[x+1][y].alive)
                grid[x+1][y].infect(virus);
            if (y>1 && grid[x][y-1].alive)
                grid[x][y-1].infect(virus);
            if (y<499 && grid[x][y+1].alive)
                grid[x][y+1].infect(virus);
        }
        if (illness == 0)
            alive = false;
        illness--;
    }

    public void infect(Infection v) {
        infected = true;
        virus = v;
        spread = v.travel() != 0;
    }
}
