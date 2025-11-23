/**
 * 
 */
/**
 * 
 */
module student.expensetracker {
	exports studentexpensetracker.domain;
    exports studentexpensetracker.exceptions;
    exports studentexpensetracker.manager;
    exports studentexpensetracker.app;
    exports studentexpensetracker.helpers;

    // Required for advanced features (like date/time API or collections
    requires java.base;
}