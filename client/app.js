const gameDisplay = document.getElementById('game-display');
const inventoryDisplay = document.getElementById('inventory-display');

const keymap = {
    'w': { command: 'move', direction: 'north' },
    's': { command: 'move', direction: 'south' },
    'a': { command: 'move', direction: 'west' },
    'd': { command: 'move', direction: 'east' },
    'g': { command: 'get' },
};

async function sendCommand(command) {
    const response = await fetch('/api/game/command', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(command),
    });
    const data = await response.json();
    render(data);
}

function render(state) {
    const { map, player, monsters, items } = state;
    let display = '';
    for (let y = 0; y < map.length; y++) {
        for (let x = 0; x < map[y].length; x++) {
            let char = map[y][x].glyph;
            const item = items.find(i => i.x === x && i.y === y);
            if (item) {
                char = item.glyph;
            }
            if (player.x === x && player.y === y) {
                char = player.glyph;
            }
            const monster = monsters.find(m => m.x === x && m.y === y);
            if (monster) {
                char = monster.glyph;
            }
            display += char;
        }
        display += '\n';
    }
    gameDisplay.textContent = display;

    let inventory = 'Inventory:\n';
    player.inventory.forEach(item => {
        inventory += `- ${item.name}\n`;
    });
    inventoryDisplay.textContent = inventory;
}

async function init() {
    const response = await fetch('/api/game/state');
    const data = await response.json();
    render(data);
}

document.addEventListener('keydown', (event) => {
    const command = keymap[event.key];
    if (command) {
        sendCommand(command);
    }
});

init();
