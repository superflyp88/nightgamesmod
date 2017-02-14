package nightgames.global.start;

import nightgames.global.Global;

public class DefaultStart implements GameStarter {

    @Override
    public void startGame() {
        Global.startMatch();
    }

}
