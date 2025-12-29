function toggleLoans() {
    const loans = document.getElementById('toggle-loans');
    if (loans) {
        loans.classList.toggle('hidden-section');
    }
}

function toggleSearch() {
    const search = document.getElementById('toggle-search');
    if (search) {
        search.classList.toggle('hidden-section');
    }
}