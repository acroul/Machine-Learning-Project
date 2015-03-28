
import java.util.Random;

/**@author Q*/

public class HighQualityRandom extends Random {
  private long u;
  private long v = 4101842887655102017L;
  private long w = 1;
  
  public HighQualityRandom() {
    this(System.nanoTime());
  }
  public HighQualityRandom(long seed) {
    u = seed ^ v;
    nextLong();
    v = u;
    nextLong();
    w = v;
    nextLong();
  }
  
  public long nextLong() {
    try {
      u = u * 2862933555777941757L + 7046029254386353087L;
      v ^= v >>> 17;
      v ^= v << 31;
      v ^= v >>> 8;
      w = 4294957665L * (w & 0xffffffff) + (w >>> 32);
      long x = u ^ (u << 21);
      x ^= x >>> 35;
      x ^= x << 4;
      long ret = (x + v) ^ w;
      return ret;
    } finally {
    }
  }
  
  protected int next(int bits) {
    return (int) (nextLong() >>> (64-bits));
  }
  
  public static void main(String[] args){
      int min = 1;
      int max = 10;
      
      HighQualityRandom random = new HighQualityRandom();
      Random random2 = new Random(System.currentTimeMillis() * (max + 5));
      
      int big = random.next(25);
      int rand = big % (max - min + 1) + min;
      
      int rand2 = random.nextInt((max - min) + 1) + min;
      
      System.out.println(rand +"  "+ rand2);
      
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      big = random.next(25);
      rand = big % (max - min + 1) + min;
      rand2 = random.nextInt((max - min) + 1) + min;
      System.out.println(rand +"  "+ rand2);
      
  }

}
