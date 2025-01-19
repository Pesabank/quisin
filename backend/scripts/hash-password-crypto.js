const crypto = require('crypto');

function hashPassword(password) {
  const salt = crypto.randomBytes(16).toString('hex');
  const hash = crypto.pbkdf2Sync(password, salt, 1000, 64, 'sha512').toString('hex');
  return `${salt}:${hash}`;
}

const password = 'Telemux01!!';
const hashedPassword = hashPassword(password);
console.log('Hashed password:', hashedPassword); 