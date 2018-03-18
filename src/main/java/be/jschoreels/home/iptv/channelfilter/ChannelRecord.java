package be.jschoreels.home.iptv.channelfilter;

import java.util.Optional;


public class ChannelRecord {

    private final String category;
    private final String descriptor;
    private final String link;

    public ChannelRecord(final String category, final String descriptor, final String link) {
        this.category = category;
        this.descriptor = descriptor;
        this.link = link;
    }

    public ChannelRecord(final String descriptor, final String link) {
        this(null, descriptor, link);
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getLink() {
        return link;
    }

    public Optional<String> getCategory() {
        return Optional.ofNullable(category);
    }
}
