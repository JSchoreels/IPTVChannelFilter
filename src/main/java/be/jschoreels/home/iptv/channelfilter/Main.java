package be.jschoreels.home.iptv.channelfilter;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {

    private static final String PLAYLIST_HEADER = "#EXTM3U";
    private static final String DESCRIPTOR_TAG = "#EXTINF:-1";

    public static void main(String[] args) throws IOException {

        final String inputFilename = args[0];
        final File inputFile = new File(inputFilename);
        final String playlistBody = Files.asCharSource(
            inputFile, Charsets.UTF_8
        ).read();

        final String[] splittedPlaylistBodyByDescriptor = playlistBody.split(DESCRIPTOR_TAG + ",");
        final List<ChannelRecord> channelRecords = new ArrayList<>(splittedPlaylistBodyByDescriptor.length);
        String category = "";
        for (int i = 1; i < splittedPlaylistBodyByDescriptor.length; i++){ // skip the first record which is PLAYLIST_HEADER
            final String currentRecordString = splittedPlaylistBodyByDescriptor[i];
            final String[] splittedRecordsByEOL = currentRecordString.split("\n");
            final String descriptor = splittedRecordsByEOL[0].replaceAll("(\\r)", "");
            if (descriptor.startsWith("•●★") && descriptor.endsWith("★●•")){
                category = descriptor.replace("★●•", "").replace("•●★", "");
            } else {
                final String link = splittedRecordsByEOL[1];
                channelRecords.add(
                    new ChannelRecord(category, descriptor, link)
                );
            }
        }

        final List<ChannelRecord> filteredChannelRecords = channelRecords.parallelStream()
            .filter(c -> !c.getDescriptor().endsWith("SD"))
            .filter(c -> !c.getDescriptor().toLowerCase().contains("arabe"))
            .filter(c -> !c.getDescriptor().toLowerCase().contains("cocuk"))
            .filter(c -> !c.getDescriptor().toLowerCase().contains("radio"))
            .filter(c -> !c.getDescriptor().startsWith("BG:"))
            .filter(c -> !c.getDescriptor().startsWith("AR:"))
            .filter(c -> !c.getDescriptor().startsWith("AF:"))
            .filter(c -> !c.getDescriptor().startsWith("IT:"))
            .filter(c -> !c.getDescriptor().startsWith("ITA:"))
            .filter(c -> !c.getDescriptor().startsWith("SWE:"))
            .filter(c -> !c.getDescriptor().startsWith("RO:"))
            .filter(c -> !c.getDescriptor().startsWith("ES:"))
            .filter(c -> !c.getDescriptor().startsWith("NL:"))
            .filter(c -> !c.getDescriptor().startsWith("DE:"))
            .filter(c -> !c.getDescriptor().startsWith("GR:"))
            .filter(c -> !c.getDescriptor().startsWith("PL:"))
            .filter(c -> !c.getDescriptor().startsWith("NL:"))
            .filter(c -> !c.getDescriptor().startsWith("AL:"))
            .filter(c -> !c.getDescriptor().startsWith("IND:"))
            .filter(c -> !c.getDescriptor().startsWith("BR:"))
            .filter(c -> !c.getDescriptor().startsWith("TUR:"))
            .filter(c -> !c.getDescriptor().startsWith("Kur:"))
            .filter(c -> !c.getDescriptor().startsWith("PTS:"))
            .filter(c -> !c.getDescriptor().startsWith("SPA:"))
            .collect(Collectors.toList());


        final List<String> playlistBodyFiltered = new ArrayList<>();
        playlistBodyFiltered.add(PLAYLIST_HEADER);
        playlistBodyFiltered.addAll(
            filteredChannelRecords.stream()
                .flatMap(channelRecord -> Stream.of(
                    DESCRIPTOR_TAG + channelRecord.getCategory().map(s ->
                        " group-title=\""+s+"\"").orElse("") + "," + channelRecord.getDescriptor(),
                    channelRecord.getLink()))
                .collect(Collectors.toList())
        );

        final String filteredPlaylistBody = playlistBodyFiltered.stream()
            .collect(Collectors.joining("\n"));

        System.out.println(String.format("Filtered %d channels over %d !", channelRecords.size() - filteredChannelRecords.size(), channelRecords.size()));


        final File outputFile = new File("filtered." + inputFile.getName());
        System.out.println("Written result in : " + outputFile.getPath());

        final FileWriter fileWriter = new FileWriter(outputFile);
        fileWriter.write(filteredPlaylistBody);
        fileWriter.close();
    }
}
