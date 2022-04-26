import wa from '@webank/wa-sdk';

const config =  {
  dev: {
    'appId': 'bdp_ide',
    'account': 'bdp_ide',
    'id': 0,
    'pageId': 0,
    'autoReport': true,
    'pagetime': false,
    'env': 'test',
  },
  sit: {
    'appId': 'bdp_ide',
    'account': 'bdp_ide',
    'id': 0,
    'pageId': 0,
    'autoReport': true,
    'pagetime': false,
    'env': 'test',
  },
  production: {
    'appId': 'bdp_ide',
    'account': 'bdp_ide',
    'id': 0,
    'pageId': 0,
    'autoReport': true,
    'pagetime': false,
    'env': 'release',
    'host': 'http://adm.webank.io/rcrm-codcs/wb-rcrm-codcs?',
  },
};

/**
 *
 * @export
 */
export default function () {
  const env = process.env.NODE_ENV;
  const analysis = config[env];
  wa.init(analysis);
  window.$Wa = wa;
}