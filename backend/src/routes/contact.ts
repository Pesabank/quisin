import { Router } from 'express';
import { ContactController } from '../controllers/contactController';
import { authenticateJWT, authorizeAdmin } from '../middleware/auth';

const router = Router();
const contactController = new ContactController();

// Public route for submitting contact form
router.post('/', contactController.submitContactForm);

// Protected routes for managing contact tickets
router.get('/tickets', authenticateJWT, authorizeAdmin, contactController.getContactTickets);
router.put('/tickets/:id', authenticateJWT, authorizeAdmin, contactController.updateTicketStatus);

export default router;
