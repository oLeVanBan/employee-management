/**
 * JWT Token Helper
 * Automatically adds JWT token to all fetch requests
 */

// Get token from localStorage
function getToken() {
    return localStorage.getItem('token');
}

// Check if user is logged in
function isLoggedIn() {
    return !!getToken();
}

// Get user info
function getUserInfo() {
    return {
        username: localStorage.getItem('username'),
        roles: JSON.parse(localStorage.getItem('roles') || '[]')
    };
}

// Logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('roles');
    window.location.href = '/login';
}

// Intercept fetch to add JWT token
const originalFetch = window.fetch;
window.fetch = function(url, options = {}) {
    const token = getToken();

    // Only add token to API requests
    if (token && url.includes('/api/')) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }

    return originalFetch(url, options)
        .then(response => {
            // Redirect to login if 401 or 403
            if (response.status === 401 || response.status === 403) {
                console.warn('Authentication failed, redirecting to login...');
                logout();
            }
            return response;
        });
};

// Show/hide UI elements based on login status
document.addEventListener('DOMContentLoaded', function() {
    console.log('Auth.js loaded'); // Debug
    console.log('Token:', getToken()); // Debug
    console.log('Username:', localStorage.getItem('username')); // Debug
    console.log('Roles:', localStorage.getItem('roles')); // Debug

    const userInfo = getUserInfo();
    const loggedIn = isLoggedIn();
    const isAdmin = userInfo.roles.includes('ROLE_ADMIN');

    console.log('Login status:', loggedIn, 'isAdmin:', isAdmin); // Debug

    // Update UI based on user role
    if (loggedIn) {
        // Show user info
        const userInfoElements = document.querySelectorAll('.user-info');
        userInfoElements.forEach(el => {
            el.textContent = `Welcome, ${userInfo.username}`;
            el.classList.remove('d-none');
        });

        // Show logout button
        const logoutButtons = document.querySelectorAll('.logout-btn');
        logoutButtons.forEach(btn => {
            btn.classList.remove('d-none');
            btn.addEventListener('click', logout);
        });

        // Hide login button
        const loginButtons = document.querySelectorAll('.login-btn');
        loginButtons.forEach(btn => btn.classList.add('d-none'));

        // Hide admin-only elements if not admin
        if (!isAdmin) {
            const adminElements = document.querySelectorAll('.admin-only');
            adminElements.forEach(el => el.classList.add('d-none'));
        }
    } else {
        // Show login button
        const loginButtons = document.querySelectorAll('.login-btn');
        loginButtons.forEach(btn => btn.classList.remove('d-none'));

        // Hide logout button
        const logoutButtons = document.querySelectorAll('.logout-btn');
        logoutButtons.forEach(btn => btn.classList.add('d-none'));

        // Hide user info
        const userInfoElements = document.querySelectorAll('.user-info');
        userInfoElements.forEach(el => el.classList.add('d-none'));
    }
});
