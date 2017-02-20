package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.credit.Credit;
import com.badlogic.ashley.core.Component;

/**
 * Created by brachiel on 20/02/2017.
 */
public class WalletComponent implements Component {
    public Credit credit = new Credit(0.0);
}
