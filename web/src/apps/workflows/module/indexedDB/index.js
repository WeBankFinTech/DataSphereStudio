import { db } from '@/common/service/db/index.js';
import project from '@/apps/workflows/service/db/project.js';
import node from '@/apps/workflows/service/db/node.js';

export default {
  name: 'workflowIndexedDB',
  events: [],
  methods: {
    async getProjectCache({projectID, key, cb}) {
      const result = await project.getProjectCache({projectID, key});
      cb(result);
    },

    addProjectCache({projectID, value}) {
      project.addProjectCache({projectID, value});
    },

    updateProjectCache({projectID, key, value, isDeep}) {
      project.updateProjectCache({projectID, key, value, isDeep});
    },
    removeProjectCache({projectID, key}) {
      project.removeProjectCache({projectID, key});
    },
    clearProjectCache() {
      db.db.project.clear();
    },
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