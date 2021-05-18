public class PlayerDriver {
    public static void main(String [] args) throws Exception{
        PlayerSample1 player = new PlayerSample1();
        System.out.println("setNameで「情報工学太郎」を入力します");
        player.setName("情報工学太郎");
        System.out.println("getName出力: " + player.getName());
        System.out.println("setColorで「black」を入力します");
        player.setColor("black");
        System.out.println("getColor出力: " + player.getColor());
    }
}