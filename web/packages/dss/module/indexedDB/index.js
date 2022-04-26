import { db } from '@dataspherestudio/shared/common/service/db/index.js';

export default {
  name: 'dssIndexedDB',
  methods: {
    deleteDb() {
      db.db.delete();
    },
  }
}