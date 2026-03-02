package pt.iscte.poo.game;

public class Time {
    
    private int xSeconds;
    
    public Time(){
        this.xSeconds = 0;

    }

    //Dá set ao tempo para 0
    public void mReset(){
        this.xSeconds = 0;
    }

    //Avança um tick
    public void mTick(){
        xSeconds++;
    }

    //Getter de segundos(apenas)
    public int mGetSeconds(){
        return xSeconds % 60;
    }

    //Getter de minutos
    public int mGetMinutos(){
        return xSeconds / 60;
    }

    //Getter de seguntos totais
    public int mGetTotalSeconds(){
        return xSeconds;
    }

    //To String
    @Override
    public String toString(){

        int xMinutos = mGetMinutos();
        int xSegundos = mGetSeconds();

        return String.format("%02d:%02d", xMinutos, xSegundos);
    }
}
