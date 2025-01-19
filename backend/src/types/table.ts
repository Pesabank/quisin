export enum TableStatus {
  AVAILABLE = 'AVAILABLE',
  OCCUPIED = 'OCCUPIED',
  RESERVED = 'RESERVED',
  CLEANING = 'CLEANING'
}

export interface Table {
  id: string;
  number: number;
  capacity: number;
  status: TableStatus;
  currentOrder?: Order;
  reservation?: Reservation;
}

export interface Order {
  id: string;
  tableId: string;
  status: OrderStatus;
  items: OrderItem[];
  createdAt: Date;
  updatedAt: Date;
}

export interface OrderItem {
  id: string;
  orderId: string;
  menuItemId: string;
  quantity: number;
  menuItem: MenuItem;
}

export interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
}

export interface Reservation {
  id: string;
  tableId: string;
  customerName: string;
  time: Date;
  partySize: number;
  status: ReservationStatus;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  PREPARING = 'PREPARING',
  READY = 'READY',
  SERVED = 'SERVED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export enum ReservationStatus {
  CONFIRMED = 'CONFIRMED',
  SEATED = 'SEATED',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW'
}
