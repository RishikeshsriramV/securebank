-- SecureBank schema (MySQL 8, InnoDB)
CREATE DATABASE IF NOT EXISTS securebank;
USE securebank;

CREATE TABLE IF NOT EXISTS users (
  user_id       INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(100) NOT NULL,
  email         VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(200) NOT NULL,
  role          ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS accounts (
  account_no   BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id      INT NOT NULL,
  account_type ENUM('SAVINGS','CURRENT') NOT NULL,
  balance      DECIMAL(15,2) NOT NULL DEFAULT 0,
  status       ENUM('ACTIVE','FROZEN') NOT NULL DEFAULT 'ACTIVE',
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_acc_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000100001;

CREATE TABLE IF NOT EXISTS transactions (
  txn_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_no    BIGINT NOT NULL,
  type          ENUM('DEPOSIT','WITHDRAW','TRANSFER_IN','TRANSFER_OUT') NOT NULL,
  amount        DECIMAL(15,2) NOT NULL,
  balance_after DECIMAL(15,2) NOT NULL,
  txn_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  remarks       VARCHAR(200),
  CONSTRAINT fk_txn_acc FOREIGN KEY (account_no) REFERENCES accounts(account_no),
  INDEX idx_txn_acc (account_no, txn_id)
) ENGINE=InnoDB;

-- Default admin  ->  email: admin@securebank.com  |  password: Admin@123
INSERT INTO users (name, email, password_hash, role)
SELECT 'Bank Admin', 'admin@securebank.com', '00112233445566778899aabbccddeeff:0dbc9af71766970efe2fbb4ed020692ec6276d23d4ba7d1dff78a72259be2bcf', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@securebank.com');
