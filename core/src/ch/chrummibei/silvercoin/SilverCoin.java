package ch.chrummibei.silvercoin;

import ch.chrummibei.silvercoin.gui.MainScreen;
import com.badlogic.gdx.Game;

public class SilverCoin extends Game {
	@Override
	public void create () {
		this.setScreen(new MainScreen());
	}
}
