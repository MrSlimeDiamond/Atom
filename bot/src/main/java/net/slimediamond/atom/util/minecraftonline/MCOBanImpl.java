package net.slimediamond.atom.util.minecraftonline;

import java.util.Date;

public class MCOBanImpl implements MCOBan {
    private MCOPlayer player;
    private MCOPlayer banner;
    private Date date;
    private String reason;

    public MCOBanImpl(MCOPlayer player, MCOPlayer banner, Date date, String reason) {
        this.player = player;
        this.banner = banner;
        this.date = date;
        this.reason = reason;
    }

    @Override
    public MCOPlayer getPlayer() {
        return player;
    }

    @Override
    public MCOPlayer getBanner() {
        return banner;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getReason() {
        return reason;
    }
}
