const API_URL = '/api/auth/';

function showNotification(message, type = 'success') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = type;
    notification.classList.add('show');
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

async function handleLogin(event) {
    event.preventDefault();
    const btn = event.submitter;
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    btn.classList.add('loading');
    btn.disabled = true;

    try {
        const response = await fetch(API_URL + 'signin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        const result = await response.json();

        if (response.ok) {
            localStorage.setItem('user', JSON.stringify(result));
            showNotification('Đăng nhập thành công!');
            setTimeout(() => {
                window.location.href = '/products';
            }, 1000);
        } else {
            showNotification(result.message || 'Sai tài khoản hoặc mật khẩu', 'error');
        }
    } catch (error) {
        showNotification('Lỗi kết nối máy chủ', 'error');
    } finally {
        btn.classList.remove('loading');
        btn.disabled = false;
    }
}

async function handleSignup(event) {
    event.preventDefault();
    const btn = event.submitter;
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    // Basic role handling
    data.role = ['user'];

    btn.classList.add('loading');
    btn.disabled = true;

    try {
        const response = await fetch(API_URL + 'signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        const result = await response.json();

        if (response.ok) {
            showNotification('Đăng ký thành công! Hãy đăng nhập.');
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);
        } else {
            showNotification(result.message || 'Lỗi đăng ký', 'error');
        }
    } catch (error) {
        showNotification('Lỗi kết nối máy chủ', 'error');
    } finally {
        btn.classList.remove('loading');
        btn.disabled = false;
    }
}

function checkAuth() {
    const user = JSON.parse(localStorage.getItem('user'));
    const authLinks = document.getElementById('auth-links');
    const userProfile = document.getElementById('user-profile');
    
    if (user && user.token) {
        if (authLinks) authLinks.style.display = 'none';
        if (userProfile) {
            userProfile.style.display = 'flex';
            document.getElementById('display-name').textContent = user.username;
        }
    } else {
        if (authLinks) authLinks.style.display = 'flex';
        if (userProfile) userProfile.style.display = 'none';
    }
}

function logout() {
    localStorage.removeItem('user');
    window.location.reload();
}

document.addEventListener('DOMContentLoaded', checkAuth);
