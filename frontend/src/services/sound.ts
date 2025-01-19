class SoundService {
  private sounds: Map<string, HTMLAudioElement> = new Map();
  private enabled: boolean = true;

  constructor() {
    // Initialize sounds
    this.loadSound('new_order', '/sounds/new-order.mp3');
    this.loadSound('waiter_hail', '/sounds/waiter-hail.mp3');
    this.loadSound('order_assigned', '/sounds/order-assigned.mp3');

    // Load sound preferences from localStorage
    this.enabled = localStorage.getItem('soundEnabled') !== 'false';
  }

  private loadSound(name: string, path: string) {
    const audio = new Audio(path);
    audio.preload = 'auto';
    this.sounds.set(name, audio);
  }

  public async play(soundName: string) {
    if (!this.enabled) return;

    const sound = this.sounds.get(soundName);
    if (sound) {
      try {
        // Reset the audio to start
        sound.currentTime = 0;
        await sound.play();
      } catch (error) {
        console.error(`Error playing sound ${soundName}:`, error);
      }
    }
  }

  public toggleSound(enabled: boolean) {
    this.enabled = enabled;
    localStorage.setItem('soundEnabled', enabled.toString());
  }

  public isEnabled(): boolean {
    return this.enabled;
  }
}

export const soundService = new SoundService();
