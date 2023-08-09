import { db } from '@dataspherestudio/shared/common/service/db/index.js';
import node from '@/workflows/service/db/node.js';

export default {
  name: 'workflowIndexedDB',
  events: [],
  methods: {
    async getNodeCache({nodeId, key, cb}) {
      const result = await node.getNodeCache({nodeId, key});
      cb(result);
    },
    addNodeCache({nodeId, value}) {
      node.addNodeCache({nodeId, value});
    },
    updateNodeCache({nodeId, key, value}) {
      node.updateNodeCache({nodeId, key, value});
    },

    removeNodeCache({nodeId, key}) {
      node.removeNodeCache({nodeId, key});
    },

    clearNodeCache() {
      db.db.node.clear();
    }
  },
};
