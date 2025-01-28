package net.slimediamond.atom.util.minecraftonline;

import java.util.Date;

public interface MCOBan {
    MCOPlayer getPlayer();
    MCOPlayer getBanner();
    Date getDate();
    String getReason();
}
