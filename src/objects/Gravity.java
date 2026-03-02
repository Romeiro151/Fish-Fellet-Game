package objects;

public interface Gravity {

    enum Weight {
        LEVE(1),
        PESADO(2);

        private final int value;

        private Weight(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }
    
    //Verificar se um objeto Gravity tem suporte por baixo
    boolean isSupported();

    //Fazer com que um objeto caia (gravidade)
    boolean mGravity();

    //Obter o peso do objeto
    public int mObjectWeigth();
}
