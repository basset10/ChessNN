package chess.server;

import com.osreboot.hvol2.direct.HvlDirect;
import com.osreboot.ridhvl2.template.HvlChronology;
import com.osreboot.ridhvl2.template.HvlDisplayWindowed;
import com.osreboot.ridhvl2.template.HvlTemplateI;

public class ServerMain extends HvlTemplateI{
	
	public static void main(String args[]) {
		HvlChronology.registerChronology(HvlDirect.class);
		new ServerMain();
	}

	public ServerMain() {
		super(new HvlDisplayWindowed(144, 300, 300, "Chess Simulator Server", false));
	}

	@Override
	public void initialize() {
		ServerNetworkManager.initialize();		
	}

	@Override
	public void update(float delta) {
		ServerNetworkManager.update(delta);
	}

}
