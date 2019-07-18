import Survey from '../../src/models/survey'

const surveyList: Survey[] = [
  {
    surveyId: '1',
    surveyName: 'Survey1',
    storeId: '',
    surveyStartDate: '2019-06-01',
    surveyEndDate: '2019-06-11',
    questions: [
      {
        questionId: '1',
        categoryEn: 'Baby1',
        categoryName: 'Baby1',
        questionDesc: 'Test Question 1-1',
        options: [
          {
            optionId: '1',
            optionDesc: 'Test Option 1-1',
            min: 1,
            max: 11111,
            isIncentiveGold: 'Y',
            isTarget: 'N',
            isIncentivePoint: 'N',
            isPhotoRequired: 'N',
            goldValue: '11',
            pointValue: '11',
          },
        ],
      },
      {
        questionId: '2',
        categoryEn: 'Baby2',
        categoryName: 'Baby2',
        questionDesc: 'Test Question 1-2',
        options: [
          {
            optionId: '2',
            optionDesc: 'Test Option 1-2',
            min: 2,
            max: 22222,
            isIncentiveGold: 'Y',
            isTarget: 'N',
            isIncentivePoint: 'N',
            isPhotoRequired: 'N',
            goldValue: '22',
            pointValue: '22',
          },
        ],
      },
      {
        questionId: '3',
        categoryEn: 'Baby3',
        categoryName: 'Baby3',
        questionDesc: 'Test Question 1-3',
        options: [
          {
            optionId: '3',
            optionDesc: 'Test Option 1-3',
            min: 33,
            max: 33333,
            isIncentiveGold: 'Y',
            isTarget: 'N',
            isIncentivePoint: 'N',
            isPhotoRequired: 'N',
            goldValue: '33',
            pointValue: '33',
          },
        ],
      },
    ],
  },
  {
    surveyId: '2',
    surveyName: 'Survey2',
    storeId: '',
    surveyStartDate: '2019-06-02',
    surveyEndDate: '2019-06-22',
    questions: [
      {
        questionId: '1',
        categoryEn: 'Baby1',
        categoryName: 'Baby1',
        questionDesc: 'Test Question 2-1',
        options: [
          {
            optionId: '1',
            optionDesc: 'Test Option 1',
            min: 1,
            max: 11111,
            isIncentiveGold: 'Y',
            isTarget: 'N',
            isIncentivePoint: 'N',
            isPhotoRequired: 'N',
            goldValue: '11',
            pointValue: '11',
          },
        ],
      },
      {
        questionId: '2',
        categoryEn: 'Baby2',
        categoryName: 'Baby2',
        questionDesc: 'Test Question 2-2',
        options: [
          {
            optionId: '2',
            optionDesc: 'Test Option 2-2',
            min: 2,
            max: 22222,
            isIncentiveGold: 'Y',
            isTarget: 'N',
            isIncentivePoint: 'N',
            isPhotoRequired: 'N',
            goldValue: '22',
            pointValue: '22',
          },
        ],
      },
      {
        questionId: '3',
        categoryEn: 'Baby3',
        categoryName: 'Baby3',
        questionDesc: 'Test Question 2-3',
        options: [
          {
            optionId: '3',
            optionDesc: 'Test Option 2-3',
            min: 33,
            max: 33333,
            isIncentiveGold: 'Y',
            isTarget: 'N',
            isIncentivePoint: 'N',
            isPhotoRequired: 'N',
            goldValue: '33',
            pointValue: '33',
          },
        ],
      },
    ],
  },
]

export default surveyList
