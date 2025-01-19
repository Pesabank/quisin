const fs = require('fs');
const { AudioContext, AudioBuffer } = require('web-audio-api');

function generateTone(frequency, duration, type = 'sine') {
    const audioContext = new AudioContext();
    const sampleRate = audioContext.sampleRate;
    const samples = duration * sampleRate;
    const buffer = audioContext.createBuffer(1, samples, sampleRate);
    const channel = buffer.getChannelData(0);
    
    for (let i = 0; i < samples; i++) {
        const t = i / sampleRate;
        switch(type) {
            case 'sine':
                channel[i] = Math.sin(2 * Math.PI * frequency * t);
                break;
            case 'square':
                channel[i] = Math.sign(Math.sin(2 * Math.PI * frequency * t));
                break;
            case 'triangle':
                channel[i] = Math.abs((t % (1/frequency)) * frequency * 2 - 1);
                break;
        }
        // Apply fade out
        if (i > samples * 0.8) {
            channel[i] *= (samples - i) / (samples * 0.2);
        }
    }
    
    return buffer;
}

// New Order Sound - Two-tone chime (pleasant alert)
const newOrderBuffer = generateTone(880, 0.15, 'sine'); // A5
const newOrderBuffer2 = generateTone(1108.73, 0.15, 'sine'); // C#6
fs.writeFileSync('../public/sounds/new-order.wav', newOrderBuffer.getWav());

// Waiter Hail Sound - Three ascending notes (urgent but not alarming)
const waiterHailBuffer = generateTone(523.25, 0.1, 'triangle'); // C5
const waiterHailBuffer2 = generateTone(659.25, 0.1, 'triangle'); // E5
const waiterHailBuffer3 = generateTone(783.99, 0.1, 'triangle'); // G5
fs.writeFileSync('../public/sounds/waiter-hail.wav', Buffer.concat([
    waiterHailBuffer.getWav(),
    waiterHailBuffer2.getWav(),
    waiterHailBuffer3.getWav()
]));

// Order Assigned Sound - Single gentle ping
const orderAssignedBuffer = generateTone(698.46, 0.2, 'sine'); // F5
fs.writeFileSync('../public/sounds/order-assigned.wav', orderAssignedBuffer.getWav());
