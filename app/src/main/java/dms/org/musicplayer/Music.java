package dms.org.musicplayer;

public class Music
{
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String musicData;

    public Music(String title, String artist, String album, /*String genre,*/ String musicData)
    {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.musicData = musicData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getMusicData() { return musicData; }

    public void setMusicData(String musicData) { this.musicData = musicData; }
}
