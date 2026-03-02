package objects;

public interface Enemy {

    // Trata da movimentação do Enemy
    void mMove();

    // Trata das interações com os peixes
    boolean mInteraction(GameCharacter yFish);
}
